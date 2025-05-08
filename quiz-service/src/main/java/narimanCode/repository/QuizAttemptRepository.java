package narimanCode.repository;

import narimanCode.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserId(Long userId);

    @Query("SELECT DISTINCT qa.userId, q.category FROM QuizAttempt qa JOIN qa.quiz q")
    List<Object[]> findUserQuizCategories();
}