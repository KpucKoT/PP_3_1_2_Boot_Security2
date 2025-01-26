package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.AdminServiceImpl;

import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminServiceImpl adminServiceImpl;

    @Autowired
    public AdminController(AdminServiceImpl adminServiceImpl) {
        this.adminServiceImpl = adminServiceImpl;
    }

    @GetMapping
    public String showAllUsers(Model model) {
        model.addAttribute("users", adminServiceImpl.getUsers());
        return "users";
    }

    @GetMapping("/{id}")
    public String showUserById(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", adminServiceImpl.getUser(id));
        return "user";
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute("user") User user) {
        return "new";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "roles", required = false) Set<Integer> roles,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "new"; // Вернуть форму с ошибками
        }
        try {
            adminServiceImpl.createUser(user, roles);
        } catch (RuntimeException e) {
            // Обработка ошибок, например, вывод сообщения об ошибке на странице
            return "redirect:/admin/new?error=" + e.getMessage();
        }
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", adminServiceImpl.getUser(id));
        return "edit";
    }

    @PatchMapping("/{id}")
    public String updateUser(@ModelAttribute("user") User user,
                             @PathVariable("id") int id,
                             @RequestParam(value = "roles", required = false) Set<String> roleNames) {
        adminServiceImpl.updateUser(id, user, roleNames);
        return "redirect:/admin";
    }

    @GetMapping("{id}/delete")
    public String deleteUser( @PathVariable("id") int id){
        adminServiceImpl.deleteUser(id);
        return "redirect:/admin";
    }

}
