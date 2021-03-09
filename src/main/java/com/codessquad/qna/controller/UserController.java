package com.codessquad.qna.controller;

import com.codessquad.qna.domain.User;
import com.codessquad.qna.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class UserController {
    UserRepository userRepository;

    @PostMapping("/users")
    public String createUser(User referenceUser) {
        User newUser = new User(referenceUser.getUserId());
        updateUserProperties(newUser, referenceUser);

        if (isRedundant(newUser)) {
            throw new IllegalStateException("이미 존재하는 사용자 아이디");
        }

        userRepository.save(newUser);

        return "redirect:/users";
    }

    private boolean isRedundant(User user) {
        return userRepository.isRedundant(user.getUserId());
    }

    @GetMapping("/users")
    public String createUserList(Model model) {
        List<User> users = userRepository.getAll();

        model.addAttribute("users", users);
        return "users/list";
    }

    @GetMapping("/users/{userId}")
    public String createProfile(@PathVariable(name = "userId") String targetId, Model model) {
        User targetUser = userRepository.getOne(targetId);
        model.addAttribute("user", targetUser);

        return "users/profile";
    }

    @GetMapping("/users/{userId}/form")
    public String createUpdateForm(@PathVariable(name = "userId") String targetId, Model model) {
        model.addAttribute("userId", targetId);
        return "users/update";
    }

    @PostMapping("/users/{userId}/update")
    public String updateUser(@PathVariable(name = "userId") String userId, User referenceUser) {
        User presentUser = userRepository.getOne(userId);

        if (!isValidPassword(presentUser.getPassword(), referenceUser.getPassword())) {
            return "redirect:/users";
        }

        updateUserProperties(presentUser, referenceUser);

        return "redirect:/users";
    }

    private boolean isValidPassword(String real, String expected) {
        return real.equals(expected);
    }

    private void updateUserProperties(User presentUser, User referenceUser) {
        presentUser.setPassword(referenceUser.getPassword());
        presentUser.setName(referenceUser.getName());
        presentUser.setEmail(referenceUser.getEmail());
    }
}
