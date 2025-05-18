package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Find questions by text containing a keyword
    List<Question> findByTextContainingIgnoreCase(String keyword);
    
    // Find random question - useful for quizzes
    @Query(value = "SELECT * FROM question ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Question findRandomQuestion();
    
    // Find random questions with a limit
    @Query(value = "SELECT * FROM question ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("limit") int limit);
    
    // Find questions by correct answer
    List<Question> findByCorrectAnswer(String correctAnswer);
}
