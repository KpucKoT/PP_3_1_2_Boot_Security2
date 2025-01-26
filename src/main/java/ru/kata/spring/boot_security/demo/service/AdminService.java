package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;

public interface AdminService {

    User getUser(int userId);
    List<User> getUsers();
    void createUser(User user, Set<Integer> roleNames);
    void updateUser(int id, User userDetails, Set<String> roleNames);
    void deleteUser(int userId);
}
