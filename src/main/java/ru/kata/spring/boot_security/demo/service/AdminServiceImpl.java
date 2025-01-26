package ru.kata.spring.boot_security.demo.service;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminServiceImpl implements AdminService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository,
                            RoleRepository roleRepository,
                            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public User getUser(int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Hibernate.initialize(user.getRoles());
        return user;
    }

    @Override
    @Transactional
    public List<User> getUsers() {
        return userRepository.findAllWithRoles();
    }

    @Override
    @Transactional
    public void createUser(User user, Set<Integer> roleNames) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Этот пользователь " + user.getUsername() + " уже существует");
        }

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new RuntimeException("Имя пользователя не может быть пустым");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("Пароль не может быть пустым");
        }

        user.setUsername(user.getUsername());
        user.setAge(user.getAge());

        Set<Role> roles = new HashSet<>();
        for (Integer roleName : roleNames) {
            Optional<Role> roleOptional = roleRepository.findById(roleName);
            roleOptional.ifPresent(roles::add);
        }

        user.setRoles(roles);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(int id, User userDetails, Set<String> roleNames) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(userDetails.getUsername());
            user.setAge(userDetails.getAge());

            Set<Role> roles = new HashSet<>();
            for (String roleName : roleNames) {
                Optional<Role> roleOptional = roleRepository.findByName(roleName);
                roleOptional.ifPresent(roles::add);
            }
            if (roles.isEmpty()) {
                Optional<Role> defaultRoleOptional = roleRepository.findByName("ROLE_USER");
                defaultRoleOptional.ifPresent(roles::add);
                if (roles.isEmpty()) {
                    throw new RuntimeException("Ошибка");
                }
            }
            user.setRoles(roles);
            user.setPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
            userRepository.save(user);
        } else {
            throw new RuntimeException("Пользователь не найден");
        }
    }

    @Override
    @Transactional
    public void deleteUser(int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new RuntimeException("Пользователь не найден");
        }
    }
}
