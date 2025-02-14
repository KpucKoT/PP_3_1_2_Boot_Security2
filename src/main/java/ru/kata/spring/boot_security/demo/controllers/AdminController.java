package ru.kata.spring.boot_security.demo.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.AdminServiceImpl;

import java.security.Principal;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminServiceImpl adminServiceImpl;

    @Autowired
    public AdminController(AdminServiceImpl adminServiceImpl) {
        this.adminServiceImpl = adminServiceImpl;
    }

    @GetMapping
    public String showAllUsers(Model model, Principal principal) {

        // Получаем список всех пользователей
        List<User> users = adminServiceImpl.getUsers();
        model.addAttribute("users", users);

        model.addAttribute("user", new User()); // Добавьте пустой объект user
        model.addAttribute("userToEdit", new User()); //Добавление пустого userToEdit

        // Получаем текущего авторизованного пользователя (администратора)
        String adminUsername = principal.getName();

        // Находим администратора по имени
        Optional<User> optionalAdmin = adminServiceImpl.getUserByUsername(adminUsername);

        if (optionalAdmin.isPresent()) {
            User admin = optionalAdmin.get();
            model.addAttribute("admin", admin);

            // Получаем строковое представление всех ролей администратора
            String adminRolesString = adminServiceImpl.getUserRolesString(admin.getId());
            // Добавляем строковое представление ролей в модель
            model.addAttribute("adminRole", adminRolesString.isEmpty() ? "Нет ролей" : adminRolesString);


        } else {
            // Если администратор не найден, добавляем null или обрабатываем ошибку
            model.addAttribute("admin", null);
            model.addAttribute("adminRole", null);
        }

        return "users"; // Возвращаем имя шаблона
    }

    @GetMapping("user/{id}")
    public String showUserById(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", adminServiceImpl.getUser(id));
        String roles = adminServiceImpl.getUserRolesString(id);
        model.addAttribute("roles", roles);
        return "user";
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute("user") User user, Model model, Principal principal) {

        Set<Role> roles = new HashSet<>(Arrays.asList(new Role(1,"ROLE_ADMIN"), new Role(2,"ROLE_USER")));
        model.addAttribute("allRoles", roles);
        String adminUsername = principal.getName();

        // Находим администратора по имени
        Optional<User> optionalAdmin = adminServiceImpl.getUserByUsername(adminUsername);

        if (optionalAdmin.isPresent()) {
            User admin = optionalAdmin.get();
            model.addAttribute("admin", admin);

            // Получаем строковое представление всех ролей администратора
            String adminRolesString = adminServiceImpl.getUserRolesString(admin.getId());
            // Добавляем строковое представление ролей в модель
            model.addAttribute("adminRole", adminRolesString.isEmpty() ? "Нет ролей" : adminRolesString);


        } else {
            // Если администратор не найден, добавляем null или обрабатываем ошибку
            model.addAttribute("admin", null);
            model.addAttribute("adminRole", null);
        }
        return "new";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "roles", required = false) Set<Integer> roles,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "new"; 
        }
        try {
            adminServiceImpl.createUser(user, roles);
        } catch (RuntimeException e) {
            return "redirect:/admin/new?error=" + e.getMessage();
        }
        return "redirect:/admin";
    }

    @PatchMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") int id,
                             @ModelAttribute User user,
                             @RequestParam(value = "role", required = false) Integer role,
                             Model model) {
        log.debug("Updating user with ID: {}", id);

        if (role == null) {
            role = 2; // Устанавливаем роль по умолчанию, если она не передана
        }

        Set<Integer> roles = Set.of(role);
        adminServiceImpl.updateUser(id, user, roles);
        return "redirect:/admin";
    }

@PostMapping("{id}/delete")
public String deleteUser( @PathVariable("id") int id){
    adminServiceImpl.deleteUser(id);
    return "redirect:/admin";
}

}
