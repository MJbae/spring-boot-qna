package com.codessquad.qna.controller;

import com.codessquad.qna.domain.User;
import com.codessquad.qna.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import static com.codessquad.qna.util.HttpSessionUtils.*;

@Controller
@RequestMapping("/users")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public String createUser(User newUser, Model model) {
        if (newUser == null) {
            model.addAttribute("errorMessage", "회원가입에 실패하였습니다.");
            return "user/form";
        }

        if (newUser.isEmpty()) {
            model.addAttribute("errorMessage", "회원가입 필드가 누락되었습니다.");
            return "user/form";
        }

        userService.join(newUser);

        return "redirect:/users";
    }

    @GetMapping
    public String showUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());

        return "user/list";
    }

    @GetMapping("/{id}")
    public String showUserInDetail(@PathVariable long id, Model model) {
        model.addAttribute("user", userService.getOneById(id));

        return "user/profile";
    }

    @GetMapping("/{id}/form")
    public String moveToUpdateForm(@PathVariable long id, Model model, HttpSession session) {
        checkSessionUser(session);

        User user = userService.getOneById(id);

        checkAccessibleSessionUser(getSessionUser(session), user);

        model.addAttribute("id", user.getId());
        model.addAttribute("userId", user.getUserId());

        return "user/update";
    }

    @PutMapping("/{id}")
    public String updateUser(@PathVariable long id, User newUserInfo, String newPassword, HttpSession session, Model model) {
        checkSessionUser(session);

        User user = userService.getOneById(id);

        checkAccessibleSessionUser(getSessionUser(session), user);

        if (newUserInfo.isEmpty()) {
            model.addAttribute("errorMessage", "비어있는 필드가 있습니다.");
            model.addAttribute("userId", user.getUserId());
            return "user/update";
        }

        userService.updateInfo(user, newUserInfo, newPassword);
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession session) {
        session.setAttribute(USER_SESSION_KEY, userService.authenticateUser(userId, password));
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(USER_SESSION_KEY);

        return "redirect:/";
    }
}
