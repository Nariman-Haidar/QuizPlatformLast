//package narimanCode.service;
//
//import narimanCode.entity.*;
//import narimanCode.repository.*;
//import narimanCode.entity.UserQuizResult;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class UserQuizResultService {
//    private final UserQuizResultRepository resultRepository;
//    private final UserRepository userRepository;
//    private final QuizRepository quizRepository;
//
//    public UserQuizResult saveResult(Long userId, Long quizId, int score) {
//        User user = userRepository.findById(userId).orElseThrow();
//        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
//        UserQuizResult result = UserQuizResult.builder()
//                .user(user)
//                .quiz(quiz)
//                .score(score)
//                .build();
//        return resultRepository.save(result);
//    }
//
//    public List<UserQuizResult> getResultsForUser(Long userId) {
//        User user = userRepository.findById(userId).orElseThrow();
//        return resultRepository.findByUser(user);
//    }
//}