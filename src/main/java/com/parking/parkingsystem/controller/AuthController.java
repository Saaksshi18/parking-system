package com.parking.parkingsystem.controller;

import com.parking.parkingsystem.model.User;
import com.parking.parkingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password.");
        if (logout != null) model.addAttribute("message", "Logged out successfully.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           RedirectAttributes redirectAttributes) {
        if (userService.emailExists(user.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email already registered.");
            return "redirect:/auth/register";
        }
        if (user.getUserType() == null) {
            user.setUserType(User.UserType.TEMPORARY);
        }
        userService.registerUser(user);
        redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
        return "redirect:/auth/login";
    }
}
