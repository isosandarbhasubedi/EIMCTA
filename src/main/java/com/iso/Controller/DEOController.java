package com.iso.Controller;

import com.iso.Model.*;
import com.iso.Repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/deo")
@PreAuthorize("hasRole('DEO')")
public class DEOController {

    private final UserRepository userRepo;

    public DEOController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // 🔹 DEO Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        // Get logged-in DEO
        User deo = userRepo.findByEmail(principal.getName());

        // Get the school of DEO
        School school = deo.getSchool();
        model.addAttribute("school", school);

        // List all educators in the same school
        List<User> educators = userRepo.findAllBySchoolAndRole(school, Role.EDUCATOR);
        model.addAttribute("educators", educators);

        return "deo/dashboard";
    }
}
