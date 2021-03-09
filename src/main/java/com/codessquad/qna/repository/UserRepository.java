package com.codessquad.qna.repository;

import com.codessquad.qna.domain.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {
    private final List<User> users = new ArrayList<>();

    public void save(User user) {
        users.add(user);
    }

    public List<User> getAll() {
        return users;
    }

    public User getOne(String targetId) {
        return users.stream()
                .filter(user -> user.getUserId().equals(targetId))
                .findAny()
                .get();
    }

    public boolean isRedundant(String targetId) {
        return users.stream()
                .anyMatch(user -> user.getUserId().equals(targetId));
    }

    public void deleteOne(User user) {
        users.remove(user);
    }
}
