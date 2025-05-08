package narimanCode.repository;

import narimanCode.entity.UserQuizResult;
import narimanCode.entity.User;
import narimanCode.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserQuizResultRepository extends JpaRepository<UserQuizResult, Long> {
    List<UserQuizResult> findByUser(User user);
    List<UserQuizResult> findByQuiz(Quiz quiz);
}