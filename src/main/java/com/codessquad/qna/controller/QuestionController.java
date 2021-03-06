package com.codessquad.qna.controller;

import com.codessquad.qna.domain.Answer;
import com.codessquad.qna.domain.Question;
import com.codessquad.qna.domain.User;
import com.codessquad.qna.exception.NotLoggedInException;
import com.codessquad.qna.service.QuestionService;
import com.codessquad.qna.util.HttpSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import static com.codessquad.qna.util.HttpSessionUtils.*;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public String createQuestion(Question newQuestion, HttpSession session) {
        if (newQuestion.isEmpty()) {
            return "question/form";
        }

        questionService.addQuestion(newQuestion, HttpSessionUtils.getUserFromSession(session));

        return "redirect:/";
    }

    @GetMapping("/form")
    public String moveToQuestionForm(Question newQuestion, HttpSession session) {
        checkSessionUser(session);

        return "question/form";
    }

    @GetMapping("/{questionId}")
    public String showQuestionInDetail(@PathVariable long questionId, Model model) {
        model.addAttribute("question", questionService.getOneById(questionId));

        return "question/show";
    }

    @GetMapping("/{questionId}/form")
    public String moveToUpdateForm(@PathVariable long questionId, Model model, HttpSession session) {
        checkSessionUser(session);

        Question question = questionService.getOneById(questionId);

        checkAccessibleSessionUser(getSessionUser(session), question);

        model.addAttribute("question", question);
        return "question/update";
    }

    @PutMapping("/{questionId}")
    public String updateQuestion(@PathVariable long questionId, Question newQuestionInfo, HttpSession session, Model model) {
        checkSessionUser(session);

        questionService.update(questionService.getOneById(questionId), newQuestionInfo, getSessionUser(session));

        return "redirect:/";
    }

    @DeleteMapping("/{questionId}")
    public String deleteQuestion(@PathVariable long questionId, HttpSession session, Model model) {
        checkSessionUser(session);

        questionService.remove(getSessionUser(session), questionService.getOneById(questionId));
        return "redirect:/";
    }
}
