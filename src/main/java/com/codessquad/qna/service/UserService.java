package com.codessquad.qna.service;

import com.codessquad.qna.domain.User;
import com.codessquad.qna.exception.JoinFailedException;
import com.codessquad.qna.exception.LoginFailedException;
import com.codessquad.qna.exception.NotFoundException;
import com.codessquad.qna.exception.UnauthorizedAccessException;
import com.codessquad.qna.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User join(User newUser) {
        if (isRedundantUser(newUser)) {
            throw new JoinFailedException("이미 존재하는 회원입니다.");
        }

        User savedUser = userRepository.save(newUser);

        if (!savedUser.equals(newUser)) {
            throw new JoinFailedException();
        }

        return savedUser;
    }

    public User authenticateUser(String userId, String password) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new LoginFailedException("존재하지 않는 회원입니다."));

        if (!user.isEqualPassword(password)) {
            throw new LoginFailedException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    public User updateInfo(User presentUser, User referenceUser, String newPassword) {
        if (!referenceUser.isEqualPassword(referenceUser.getPassword())) {
            throw new UnauthorizedAccessException("비밀번호가 일치하지 않습니다.");
        }

        presentUser.updateUserInfo(referenceUser, newPassword);
        return userRepository.save(presentUser);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getOneById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    private boolean isRedundantUser(User user) {
        User redundantUser = userRepository.findByUserId(user.getUserId()).orElse(null);
        return redundantUser != null;
    }
}
