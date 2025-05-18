package com.lms.LearningManagementSystem.repository;
import com.lms.LearningManagementSystem.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // Find quizzes by title containing keyword
    List<Quiz> findByTitleContainingIgnoreCase(String keyword);
    
    // Find quizzes by description containing keyword
    List<Quiz> findByDescriptionContainingIgnoreCase(String keyword);
    
    // Find quizzes with total marks greater than or equal to a value
    List<Quiz> findByTotalMarksGreaterThanEqual(Long marks);
    
    // Find quizzes with total marks less than or equal to a value
    List<Quiz> findByTotalMarksLessThanEqual(Long marks);
    
    // Find quizzes that contain a specific question
    @Query("SELECT q FROM Quiz q JOIN q.questions ques WHERE ques.id = :questionId")
    List<Quiz> findByQuestionId(@Param("questionId") Long questionId);
    
    // Search quizzes by keywords in title or description
    @Query("SELECT q FROM Quiz q WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Quiz> searchQuizzes(@Param("keyword") String keyword);
}
