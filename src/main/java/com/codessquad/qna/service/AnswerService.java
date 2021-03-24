package com.codessquad.qna.service;

import com.codessquad.qna.domain.Answer;
import com.codessquad.qna.domain.Question;
import com.codessquad.qna.domain.User;
import com.codessquad.qna.exception.NotFoundException;
import com.codessquad.qna.repository.AnswerRepository;
import com.codessquad.qna.repository.QuestionRepository;
import com.codessquad.qna.util.HttpSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.codessquad.qna.util.HttpSessionUtils.checkAccessibleSessionUser;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public AnswerService(AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    public Answer getOneById(long answerId) {
        return answerRepository.findByAnswerIdAndDeletedFalse(answerId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 답변입니다."));
    }

    public void create(Long id, String contents, HttpSession session) {
        User loginUser = HttpSessionUtils.getUserFromSession(session);

        Question question = questionRepository.findByQuestionIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 질문입니다."));

        answerRepository.save(new Answer(question, contents, loginUser));
    }

    public void remove(User sessionUser, Answer answer) {

        checkAccessibleSessionUser(sessionUser, answer);

        answer.deleted();
        answerRepository.save(answer);
    }

    public List<Answer> findAll() {
        return answerRepository.findAllByAndDeletedFalse();
    }
}
