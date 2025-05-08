package narimanCode.repository;

import narimanCode.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByUsername(String username);

    @Query("SELECT p FROM Person p JOIN FETCH p.role WHERE p.username = :username")
    Optional<Person> findByUsernameWithRole(@Param("username") String username);

    @Query("SELECT p FROM Person p JOIN FETCH p.role WHERE p.id = :id")
    Optional<Person> findByIdWithRole(@Param("id") Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Person> findByEmail(String email);
}