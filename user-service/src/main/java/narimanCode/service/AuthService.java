package narimanCode.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import narimanCode.dto.AuthRequest;
import narimanCode.dto.AuthResponse;
import narimanCode.dto.PasswordResetRequest;
import narimanCode.dto.RegistrationDTO;
import narimanCode.entity.Person;
import narimanCode.entity.Role;
import narimanCode.exception.BadRequestException;
import narimanCode.repository.PersonRepository;
import narimanCode.repository.RoleRepository;
import narimanCode.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordResetService passwordResetService;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegistrationDTO request) {
        log.info("Starting registration for username: {}", request.getUsername());

        // Validate username and email uniqueness
        if (personRepository.existsByUsername(request.getUsername())) {
            log.warn("Username {} already exists", request.getUsername());
            throw new BadRequestException("Username already exists");
        }
        if (personRepository.existsByEmail(request.getEmail())) {
            log.warn("Email {} already exists", request.getEmail());
            throw new BadRequestException("Email already exists");
        }

        // Create Person entity
        Person person = new Person();
        person.setUsername(request.getUsername());
        person.setPassword(passwordEncoder.encode(request.getPassword())); // ✅ Ensure password is properly hashed
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setEmail(request.getEmail());
        person.setAccountLocked(false);

        // Normalize role (accept "USER", "ADMIN", "ROLE_USER", "ROLE_ADMIN")
        String inputRole = request.getRole() != null ? request.getRole().toUpperCase() : "USER"; // ✅ Prevents NullPointerException
        String roleName = inputRole.startsWith("ROLE_") ? inputRole : "ROLE_" + inputRole;

        if (!roleName.equals("ROLE_USER") && !roleName.equals("ROLE_ADMIN")) {
            log.warn("Invalid role provided: {}", request.getRole());
            throw new BadRequestException("Invalid role: " + request.getRole() + ". Must be USER or ADMIN");
        }

        // Fetch or create role
        log.debug("Fetching or creating role: {}", roleName);
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role(roleName);
                    log.debug("Saving new role: {}", roleName);
                    return roleRepository.save(newRole);
                });
        person.setRole(role);

        // Save person
        log.debug("Saving person: {}", person.getUsername());
        personRepository.save(person);

        // ✅ Instead of authentication, directly fetch user details for JWT creation
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String jwt = jwtTokenProvider.generateToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        log.info("Registration successful for user: {}", person.getUsername());
        return AuthResponse.builder()
                .token(jwt)
                .username(person.getUsername())
                .userId(person.getId())
                .roles(roles)
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Starting login for username: {}", request.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtTokenProvider.generateToken(userDetails);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Person person = personRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

        log.info("Login successful for user: {}", person.getUsername());
        return AuthResponse.builder()
                .token(jwt)
                .username(person.getUsername())
                .userId(person.getId())
                .roles(roles)
                .build();
    }

    /*public void logout(String token) {
        log.info("Processing logout request");
        String jwt = token.substring(7);
        tokenBlacklistService.blacklistToken(jwt, jwtTokenProvider.getExpirationFromToken(jwt));
        log.info("Token blacklisted successfully");
    }


     */
    public void logout(String token) {
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }
        long expiration = jwtTokenProvider.getExpirationFromToken(token);

        // ✅ Use injected instance, NOT static method reference
        tokenBlacklistService.blacklistToken(token, expiration);

        log.info("Token blacklisted successfully");
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request){
        log.info("Processing password reset for token: {}", request.getToken());
        if (!passwordResetService.validateResetToken(request.getToken())) {
            log.warn("Invalid or expired password reset token");
            throw new BadRequestException("Invalid or expired password reset token");
        }

        String email = passwordResetService.getEmailFromToken(request.getToken());
        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        person.setPassword(passwordEncoder.encode(request.getNewPassword()));
        personRepository.save(person);

        passwordResetService.invalidateToken(request.getToken());
        log.info("Password reset successful for user: {}", person.getUsername());
    }
}