package com.iso.Controller;

import com.iso.Model.*;
import com.iso.Repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/principal")
@PreAuthorize("hasRole('PRINCIPAL')")
public class PrincipalController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public PrincipalController(UserRepository userRepo,
                               PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    // 🔹 Principal Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        model.addAttribute("school", user.getSchool());
        model.addAttribute("educators", userRepo.findAllBySchoolAndRole(user.getSchool(), Role.EDUCATOR));
        return "principal/dashboard";
    }

    // 🔹 Show Create Educator Form
    @GetMapping("/educators/create")
    public String showCreateEducatorForm(Model model) {
        model.addAttribute("user", new User());
        return "principal/create-educator";
    }

    // 🔹 Save Educator
    @PostMapping("/educators/create")
    public String createEducator(@ModelAttribute User user, Principal principal, Model model) {

        User principalUser = userRepo.findByEmail(principal.getName());
        School school = principalUser.getSchool();

        if (userRepo.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email already exists!");
            return "principal/create-educator";
        }

        user.setRole(Role.EDUCATOR);
        user.setSchool(school);
        user.setPassword(encoder.encode(user.getPassword()));

        userRepo.save(user);
        return "redirect:/principal/dashboard";
    }
    
    @GetMapping("/change-password")
    public String showChangePasswordPage() {
        return "auth/change-password";
    }
    
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication authentication,
            Model model) {

        User user = userRepo.findByEmail(authentication.getName());

        // 1️⃣ Check current password
        if (!encoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("error", "Current password is incorrect.");
            return "auth/change-password";
        }

        // 2️⃣ Check new password match
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            return "auth/change-password";
        }

        // 3️⃣ Update password
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);

        model.addAttribute("success", "Password changed successfully.");
        return "auth/change-password";
    }


}
