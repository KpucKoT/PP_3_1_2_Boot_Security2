package ru.kata.spring.boot_security.demo.controllers;//package ru.kata.spring.boot_security.demo.controllers;
//
//
//import jakarta.validation.Valid;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import ru.kata.spring.boot_security.demo.model.User;
//import ru.kata.spring.boot_security.demo.service.AdminServiceImpl;
//
//import java.util.Optional;
//import java.util.Set;
//
//@Slf4j
//@RestController
//@RequestMapping("/admin")
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminRestController {
//
//    private final AdminServiceImpl adminService;
//
//    public AdminRestController(AdminServiceImpl adminService) {
//        this.adminService = adminService;
//    }

//    @PatchMapping("/edit/{id}")
//    public String updateUser(@PathVariable("id") int id,
//                             @Valid @ModelAttribute("user") User user,
//                             BindingResult bindingResult,
//                             @RequestParam(value = "roles", required = false) Set<Integer> roles,
//                             Model model) {
//
//        if (bindingResult.hasErrors()) {
//            log.warn("Validation errors: {}", bindingResult.getAllErrors());
//            model.addAttribute("allRoles", adminService.getAllRoles());
//            return "editForm"; // Отображаем форму с ошибками
//        }
//
//        try {
//            adminService.updateUser(id, user, roles);
//            log.info("User with ID {} updated successfully.", id);
//            return "redirect:/admin"; // Перенаправляем на список пользователей
//        } catch (Exception e) {
//            log.error("Error updating user with ID: {}", id, e);
//            model.addAttribute("errorMessage", "Произошла ошибка при обновлении пользователя: " + e.getMessage());
//            model.addAttribute("allRoles", adminService.getAllRoles());
//
//            return "editForm"; // Отображаем форму с ошибкой
//        }
//    }


//    @GetMapping("/{id}")
//    public ResponseEntity<User> getUserById(@PathVariable int id) {
//        // Получаем пользователя по ID напрямую
//        User user = adminService.getUser(id); // Предполагается, что findById возвращает User или выбрасывает исключение
//
//        if (user != null) {
//            return ResponseEntity.ok(user); // Возвращаем пользователя, если он найден
//        } else {
//            return ResponseEntity.notFound().build(); // Возвращаем 404, если пользователь не найден
//        }
//    }

//    @PatchMapping("/edit/{id}")
//    public ResponseEntity<String> updateUser(@PathVariable("id") int id, @RequestBody User user,
//                                             @RequestParam(value = "roles", required = false) Set<Integer> roles) {
//        adminService.updateUser(id, user, roles);
//        return ResponseEntity.ok("User updated successfully");
//    }
//}

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.AdminServiceImpl;

@RestController
@RequestMapping("admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminRestController {

    private final AdminServiceImpl adminService;

    @Autowired
    public AdminRestController(AdminServiceImpl adminService) {
        this.adminService = adminService;
    }

    // Получение пользователя по ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = adminService.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}
