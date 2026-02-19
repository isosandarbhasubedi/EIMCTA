package com.iso.Controller;

import com.iso.Model.*;
import com.iso.Repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/coordinator")
@PreAuthorize("hasRole('COORDINATOR')")
public class CoordinatorController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public CoordinatorController(UserRepository userRepo,
                                 PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    // 🔹 Coordinator Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        model.addAttribute("school", user.getSchool());
        model.addAttribute("educators", userRepo.findAllBySchoolAndRole(user.getSchool(), Role.EDUCATOR));
        return "coordinator/dashboard";
    }

    // 🔹 Show Create Educator Form
    @GetMapping("/educators/create")
    public String showCreateEducatorForm(Model model) {
        model.addAttribute("user", new User());
        return "coordinator/create-educator";
    }

    // 🔹 Save Educator
    @PostMapping("/educators/create")
    public String createEducator(@ModelAttribute User user, Principal principal, Model model) {

        User coordinatorUser = userRepo.findByEmail(principal.getName());
        School school = coordinatorUser.getSchool();

        if (userRepo.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email already exists!");
            return "coordinator/create-educator";
        }

        user.setRole(Role.EDUCATOR);
        user.setSchool(school);
        user.setPassword(encoder.encode(user.getPassword()));

        userRepo.save(user);
        return "redirect:/coordinator/dashboard";
    }
}
