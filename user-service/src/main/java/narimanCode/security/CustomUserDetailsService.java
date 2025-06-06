package narimanCode.security;

import lombok.RequiredArgsConstructor;
import narimanCode.entity.Person;
import narimanCode.repository.PersonRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personRepository.findByUsernameWithRole(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        String role = person.getRole().getName(); // e.g., "ROLE_USER" or "ROLE_ADMIN"
        return new User(
                person.getUsername(),
                person.getPassword(),
                person.isAccountLocked() ? Collections.emptyList() : 
                    Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}