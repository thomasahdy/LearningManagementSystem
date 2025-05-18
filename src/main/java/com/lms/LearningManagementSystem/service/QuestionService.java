package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Question;
import com.lms.LearningManagementSystem.repository.QuestionRepository;
import org.apache.commons.math3.exception.NullArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class QuestionService {


    private final QuestionRepository questionRepository;

    private final Random random = new Random();

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public Question getRandomQuestion() {
        List<Question> questions = questionRepository.findAll();
        if (questions.isEmpty()) throw new NullArgumentException();
        return questions.get(random.nextInt(questions.size()));
    }
}

