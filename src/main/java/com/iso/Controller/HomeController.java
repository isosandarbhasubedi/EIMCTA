package com.iso.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Login page
    @GetMapping("/login")
    public String login() {
        return "login"; // Thymeleaf template: login.html
    }

    // Optional: home page or welcome page
    @GetMapping("/")
    public String home() {
        return "redirect:/login"; // redirect to login
    }

    
    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            for (GrantedAuthority authority : auth.getAuthorities()) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    return "redirect:/admin/dashboard";
                } else if (authority.getAuthority().equals("ROLE_USER")) {
                    return "redirect:/user/dashboard";
                }else if (authority.getAuthority().equals("ROLE_EMPLOYEE")) {
                    return "redirect:/employee/dashboard";
                }
                
            }
        }
        return "redirect:/login";
    }

    
    // Optional: access denied page
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied"; // create access-denied.html if needed
    }
}
