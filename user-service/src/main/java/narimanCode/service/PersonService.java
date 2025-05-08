package narimanCode.service;

import lombok.RequiredArgsConstructor;
import narimanCode.dto.PersonDTO;
import narimanCode.entity.Person;
import narimanCode.exception.ResourceNotFoundException;
import narimanCode.repository.PersonRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    @Transactional(readOnly = true)
    public PersonDTO getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person = personRepository.findByUsernameWithRole(username)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "username", username));
        return mapToDTO(person);
    }

    @Transactional(readOnly = true)
    public PersonDTO getUserById(Long id) {
        Person person = personRepository.findByIdWithRole(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", id));
        return mapToDTO(person);
    }

    @Transactional
    public void lockUser(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", id));
        person.setAccountLocked(true);
        personRepository.save(person);
    }

    @Transactional
    public void unlockUser(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", id));
        person.setAccountLocked(false);
        personRepository.save(person);
    }

    private PersonDTO mapToDTO(Person person) {
        PersonDTO dto = new PersonDTO();
        dto.setId(person.getId());
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setUsername(person.getUsername());
        dto.setEmail(person.getEmail());
        dto.setRole(person.getRole().getName());
        dto.setAccountLocked(person.isAccountLocked());
        return dto;
    }
}