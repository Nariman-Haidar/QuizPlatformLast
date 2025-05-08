package narimanCode.repository;

import narimanCode.entity.PasswordResetToken;
import narimanCode.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Stream<PasswordResetToken> findAllByPerson(Person person);

    Optional<PasswordResetToken> findByTokenAndPerson(String token, Person person);

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate <= :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.person = :person")
    void deleteByPerson(@Param("person") Person person);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM PasswordResetToken t " +
            "WHERE t.token = :token AND t.expiryDate > :now")
    boolean isTokenValid(@Param("token") String token, @Param("now") LocalDateTime now);
}