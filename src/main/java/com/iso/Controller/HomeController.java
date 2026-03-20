package com.iso.Controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iso.Model.ContactMessage;
import com.iso.Model.OrganizationRegistration;
import com.iso.Model.PasswordResetToken;
import com.iso.Model.User;
import com.iso.Repository.AuditLogRepository;
import com.iso.Repository.ContactMessageRepository;
import com.iso.Repository.OrganizationRegistrationRepository;
import com.iso.Repository.PasswordResetTokenRepo;
import com.iso.Repository.ProvinceAuditLogRepository;
import com.iso.Repository.ProvinceRepository;
import com.iso.Repository.SchoolAuditLogRepository;
import com.iso.Repository.SchoolRepository;
import com.iso.Repository.SchoolUserAuditLogRepository;
import com.iso.Repository.UserRepository;
import com.iso.Service.AuditService;
import com.iso.Service.EmailService;
import com.iso.Service.ProvinceAuditService;
import com.iso.Service.SchoolAuditService;
import com.iso.Service.SchoolUserAuditService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

	
	 private final SchoolRepository schoolRepo;
	    private final UserRepository userRepo;
	    private final ProvinceRepository provinceRepo;
	    private final AuditLogRepository auditlogRepo;
	    private final ProvinceAuditLogRepository provinceAuditRepo;
	    private final PasswordEncoder encoder;
	    private final AuditService auditService;
	    private final ProvinceAuditService provinceAuditService;
	    private final SchoolAuditLogRepository schoolAuditLogRepository; 
	    private final SchoolAuditService schoolAuditService;
	    private final SchoolUserAuditService schoolUserAuditService;
	    private final SchoolUserAuditLogRepository schoolUserAuditLogRepository;
	    private final EmailService emailService;
	    private final OrganizationRegistrationRepository organizationRegistrationRepository;
	    private final ContactMessageRepository contactMessageRepository;
	    private final PasswordResetTokenRepo tokenRepo;


	    public HomeController(SchoolRepository schoolRepo,
	                           UserRepository userRepo,
	                           PasswordEncoder encoder,
	                           ProvinceRepository provinceRepo,
	                           AuditLogRepository auditlogRepo,
	                           ProvinceAuditLogRepository provinceAuditRepo,
	                           AuditService auditService,
	                           ProvinceAuditService provinceAuditService,
	                           SchoolAuditLogRepository schoolAuditLogRepository,
	                           SchoolAuditService schoolAuditService,
	                           SchoolUserAuditService schoolUserAuditService,
	                           SchoolUserAuditLogRepository schoolUserAuditLogRepository,
	                           EmailService emailService,
	                           OrganizationRegistrationRepository organizationRegistrationRepository,
	                           ContactMessageRepository contactMessageRepository,
	                           PasswordResetTokenRepo tokenRepo
	                           ) {
	        this.schoolRepo = schoolRepo;
	        this.userRepo = userRepo;
	        this.encoder = encoder;
	        this.provinceRepo = provinceRepo;
	        this.auditlogRepo = auditlogRepo;
	        this.provinceAuditRepo = provinceAuditRepo;
	        this.auditService = auditService;
	        this.provinceAuditService = provinceAuditService;
	        this.schoolAuditLogRepository = schoolAuditLogRepository;
	        this.schoolAuditService = schoolAuditService;
	        this.schoolUserAuditService = schoolUserAuditService;
	        this.schoolUserAuditLogRepository = schoolUserAuditLogRepository; 
	        this.emailService = emailService;
	        this.organizationRegistrationRepository = organizationRegistrationRepository;
	        this.contactMessageRepository = contactMessageRepository;
	        this.tokenRepo = tokenRepo;
	    }
    // Login page
    @GetMapping("/login")
    public String login() {
        return "login"; // Thymeleaf template: login.html
    }

    // Optional: home page or welcome page
    @GetMapping("/")
    public String home(
    		Model model) {
    	
    	if (!model.containsAttribute("organization")) {
            model.addAttribute("organization", new OrganizationRegistration());
        }
    	
    	model.addAttribute("contactMessage", new ContactMessage());
        return "landing.html"; // redirect to login
    }

    
 // POST: Save Registration
    @PostMapping("/register-organization")
    public String registerOrganization(
            @ModelAttribute OrganizationRegistration organization,
            Model model,
            RedirectAttributes redirectAttributes) {

    	 
            organizationRegistrationRepository.save(organization);
           
         // Flash attribute (survives redirect)
            redirectAttributes.addFlashAttribute("successMessage",
                    "Organization registered successfully!"
                    +" "+"Will contact you within 12 hours");

       
        return "redirect:/";   // your thymeleaf page name (register.html)
    }
    
 // Handle contact form submission
    @PostMapping("/contact")
    public String submitContact(RedirectAttributes redirectAttributes,
            @ModelAttribute ContactMessage contactMessage) {

        contactMessageRepository.save(contactMessage);

     // Flash attribute (survives redirect)
        redirectAttributes.addFlashAttribute("successMessage",
                "Contact Request Successfully Sent!"
                +" "+"Will contact you within 12 hours");

        
        return "redirect:/";
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
    
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }
    
    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam String email,
            HttpServletRequest request,
            Model model) {

        User user = userRepo.findByEmail(email);

        if (user == null) {
            model.addAttribute("error", "No account found with this email.");
            return "auth/forgot-password";
        }

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = tokenRepo.findByUser(user)
                .orElse(new PasswordResetToken());

        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        tokenRepo.save(resetToken);

        String resetLink = request.getRequestURL().toString()
                .replace(request.getServletPath(), "")
                + "/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getUsername(),
                resetLink
        );

        model.addAttribute("success", "Password reset link sent to your email.");
        return "auth/forgot-password";
    }
    
    
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {

        Optional<PasswordResetToken> optionalToken = tokenRepo.findByToken(token);

        if (optionalToken.isEmpty() ||
            optionalToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {

            model.addAttribute("error", "Invalid or expired token.");
            return "auth/reset-password";
        }

        model.addAttribute("token", token);
        return "auth/reset-password";
    }
    
    
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {

        Optional<PasswordResetToken> optionalToken = tokenRepo.findByToken(token);

        if (optionalToken.isEmpty()) {
            model.addAttribute("error", "Invalid token.");
            return "auth/reset-password";
        }

        PasswordResetToken resetToken = optionalToken.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Token has expired.");
            return "auth/reset-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("token", token);
            return "auth/reset-password";
        }

        // 🔐 Add same strength validation here
        String passwordRegex =
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$";

        if (!newPassword.matches(passwordRegex)) {
            model.addAttribute("error",
                    "Password must contain uppercase, lowercase, number and special character.");
            model.addAttribute("token", token);
            return "auth/reset-password";
        }

        User user = resetToken.getUser();
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);

        tokenRepo.delete(resetToken); // delete token after use

        model.addAttribute("success", "Password reset successfully.");
        return "login";
    }




}
