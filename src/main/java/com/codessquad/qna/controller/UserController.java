package com.codessquad.qna.controller;

import com.codessquad.qna.domain.User;
import com.codessquad.qna.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public String createUser(User newUser) {

        if (!isValidUser(newUser)) {
            return "user/form";
        }

        //TODO: join의 반환값으로 id를 받아서 정상 join 여부 확인
        userService.join(newUser);

        return "redirect:/users";
    }

    private boolean isValidUser(User user) {
        if (user == null){
            return false;
        }
        if ("".equals(user.getUserId()) || user.getUserId() == null) {
            return false;
        }
        if ("".equals(user.getEmail()) || user.getEmail() == null) {
            return false;
        }
        if ("".equals(user.getPassword()) || user.getPassword() == null) {
            return false;
        }
        if ("".equals(user.getName()) || user.getName() == null) {
            return false;
        }

        return true;
    }

    @GetMapping
    public String showUsers(Model model) {
        model.addAttribute("users", userService.showAll());

        return "/user/list";
    }

    @GetMapping("/{id}")
    public String showUserInDetail(@PathVariable long id, Model model) {
        model.addAttribute("user", userService.showOneById(id).orElse(null));

        return "/user/profile";
    }

    @GetMapping("/{id}/form")
    public String passUserId(@PathVariable long id, Model model) {
        User user = userService.showOneById(id).orElse(null);

        model.addAttribute("id", user.getId());
        model.addAttribute("userId", user.getUserId());

        return "/user/update";
    }

    @PutMapping("/{id}")
    public String updateUser(@PathVariable long id, User referenceUser) {
        User presentUser = userService.showOneById(id).orElse(null);

        if(!isValidUser(presentUser)){
            logger.info("isValidUser");
            return "redirect:/users";
        }

        if (!presentUser.isEqualPassword(referenceUser.getPassword())) {
            logger.info("isEqualPassword");
            return "redirect:/users";
        }
        logger.info("updateUserProperties");
        userService.updateInfo(presentUser, referenceUser);
        return "redirect:/users";
    }
}
