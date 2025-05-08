package narimanCode.controller;

import lombok.RequiredArgsConstructor;
import narimanCode.dto.PersonDTO;
import narimanCode.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final PersonService personService;

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PersonDTO> getCurrentUserProfile() {
        PersonDTO profile = personService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonDTO> getUserById(@PathVariable Long id) {
        PersonDTO user = personService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> lockUser(@PathVariable Long id) {
        personService.lockUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unlockUser(@PathVariable Long id) {
        personService.unlockUser(id);
        return ResponseEntity.ok().build();
    }
}