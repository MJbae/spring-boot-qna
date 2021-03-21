package com.codessquad.qna.controller;

import com.codessquad.qna.domain.Answer;
import com.codessquad.qna.domain.Result;
import com.codessquad.qna.domain.User;
import com.codessquad.qna.service.AnswerService;
import com.codessquad.qna.util.HttpSessionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/questions/{questionId}/answers")
public class AnswerController {
    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping
    public String create(@PathVariable Long questionId, String contents, HttpSession session) {
        if (!HttpSessionUtils.isLoginUser(session)) {
            return "users/login";
        }

        answerService.create(questionId, contents, session);
        return "redirect:/questions/" + questionId;
    }

    @DeleteMapping("/{answerId}")
    public String deleteAnswer(@PathVariable long answerId, HttpSession session, Model model) {
        Answer answer = answerService.getOneById(answerId).orElse(null);
        Result result = checkSession(session, answer);

        if (!result.isValid()) {
            model.addAttribute("errorMessage", result.getErrorMessage());
            return "user/login";
        }

        answerService.remove(answerId);
        return "redirect:/questions/{questionId}";
    }

    private Result checkSession(HttpSession session, Answer answer) {
        if (!HttpSessionUtils.isLoginUser(session)) {
            return Result.fail("로그인이 필요합니다.");
        }

        User sessionUser = HttpSessionUtils.getUserFromSession(session);
        if (!answer.isEqualWriter(sessionUser)) {
            return Result.fail("자신이 쓴 글만 수정 및 삭제가 가능합니다.");
        }

        return Result.ok();
    }

}
