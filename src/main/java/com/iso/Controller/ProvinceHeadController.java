package com.iso.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.iso.Model.Role;
import com.iso.Model.School;
import com.iso.Model.User;
import com.iso.Repository.ProvinceRepository;
import com.iso.Repository.SchoolRepository;
import com.iso.Repository.UserRepository;
import com.iso.Service.EmailService;
import com.iso.Service.ProvinceAuditService;
import com.iso.Service.SchoolAuditService;
import com.iso.Service.SchoolUserAuditService;

import jakarta.servlet.http.HttpServletRequest;



@Controller
@RequestMapping("/provincehead")
@PreAuthorize("hasRole('PROVINCE_HEAD')")
public class ProvinceHeadController {
	
	 private final SchoolRepository schoolRepo;
	 private final UserRepository userRepo;
	    private final ProvinceRepository provinceRepo;
	    private final ProvinceAuditService provinceAuditService;
	    private final SchoolAuditService schoolAuditService;
	    private final SchoolUserAuditService schoolUserAuditService;
	    private final EmailService emailService;
	    private final PasswordEncoder encoder;
	    
	    public ProvinceHeadController(SchoolRepository schoolRepo,
                UserRepository userRepo,
                PasswordEncoder encoder,
                ProvinceRepository provinceRepo,
                ProvinceAuditService provinceAuditService,
                SchoolAuditService schoolAuditService,
                SchoolUserAuditService schoolUserAuditService,
                EmailService emailService) {
           this.schoolRepo = schoolRepo;
           this.userRepo = userRepo;
           this.encoder = encoder;
           this.provinceRepo = provinceRepo;
           this.provinceAuditService = provinceAuditService;
           this.schoolAuditService = schoolAuditService;
           this.schoolUserAuditService = schoolUserAuditService;
           this.emailService = emailService;
          }
	    
	    @GetMapping("/dashboard")
	    public String provinceheadDashboard() {
	        return "province-head/dashboard";
	    }
	    
	    @GetMapping("/province-users/create")
	    public String showCreateProvinceUserForm(Model model) {
	    	
	    	model.addAttribute("roles", new Role[]{
	                Role.CONSULTANT,
	                Role.CRO,
	                Role.EDEO
	        });

	        model.addAttribute("user", new User());

	        return "province-head/create-province-user";
	    }

	    @PostMapping("/province-users/create")
	    public String createProvinceUser(
	            @ModelAttribute User user,
	            Authentication authentication,
	            HttpServletRequest request) {

	        User loggedUser = userRepo.findByEmail(authentication.getName());

	        // 🔐 Only allow specific province-level roles
	        if (user.getRole() != Role.CONSULTANT &&
	            user.getRole() != Role.CRO &&
	            user.getRole() != Role.EDEO) {
	            return "redirect:/province-head/schools";
	        }

	        // Assign the same province as the logged-in province head
	        user.setProvince(loggedUser.getProvince());
	        user.setSchool(null); // ❗ ensure no school assigned
	        user.setActive(true);
	        userRepo.save(user);

	        // 🔥 Log creation in province audit
	        String ipAddress = request.getRemoteAddr();
	        provinceAuditService.log(
	            "CREATE_USER",
	            "PROVINCE_USER",
	            user.getId(),
	            authentication.getName(),
	            ipAddress,
	            "Created province user with email: " + user.getEmail() +
	            " and role: " + user.getRole()
	        );

	        return "redirect:/provincehead/province-users";
	    }

	    
	    @GetMapping("/province-users")
	    public String viewProvinceUsers(
	            Authentication authentication,
	            Model model) {

	        User loggedUser = userRepo.findByEmail(authentication.getName());

	        List<Role> roles = List.of(
	                Role.CONSULTANT,
	                Role.CRO,
	                Role.EDEO
	        );

	        List<User> users =
	                userRepo.findByProvinceAndRoleIn(
	                        loggedUser.getProvince(),
	                        roles
	                );

	        model.addAttribute("users", users);

	        return "province-head/view-province-users";
	    }


	    
	    @GetMapping("/schools")
	    public String viewSchools(Authentication authentication, Model model) {

	        User loggedUser = userRepo.findByEmail(authentication.getName());

	        List<School> schools =
	                schoolRepo.findByProvince(loggedUser.getProvince());
	        model.addAttribute("schools", schoolRepo.findAll());
	        model.addAttribute("schools", schools);

	        return "province-head/schools";
	    }
	    
	    @GetMapping("/schools/create")
	    public String showCreateSchoolForm(Model model) {
	        model.addAttribute("school", new School());
	        return "province-head/create-school";
	    }

	    
	    @PostMapping("/schools/create")
	    public String createSchool(@ModelAttribute School school,
	                               Authentication authentication,
	                               @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
	                               HttpServletRequest request) {

	        User loggedUser = userRepo.findByEmail(authentication.getName());

	        try {
	            if (logoFile != null && !logoFile.isEmpty()) {
	                String uploadDir = "uploads/";
	                File uploadPath = new File(uploadDir);
	                if (!uploadPath.exists()) uploadPath.mkdirs();

	                String fileName = System.currentTimeMillis() + "_" + logoFile.getOriginalFilename();
	                Path filePath = Paths.get(uploadDir + fileName);
	                Files.copy(logoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	                school.setLogo(fileName);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        // 🔥 Automatically assign province
	        school.setProvince(loggedUser.getProvince());
	        schoolRepo.save(school);

	        // 🔥 SCHOOL AUDIT LOG
	        schoolAuditService.log(
	                "CREATE_SCHOOL",
	                "SCHOOL",
	                school.getId(),
	                authentication.getName(),
	                request.getRemoteAddr(),
	                "Created school: " + school.getName() +
	                " in province: " + loggedUser.getProvince().getName()
	        );

	        return "redirect:/provincehead/schools";
	    }

	    
	    // 🔹 Show Create School User Form
	    @GetMapping("/schools/{schoolId}/users/create")
	    public String showCreateSchoolUserForm(
	            @PathVariable Long schoolId,
	            Model model) {

	        School school = schoolRepo.findById(schoolId)
	                .orElseThrow(() -> new RuntimeException("School not found"));

	        model.addAttribute("school", school);
	        model.addAttribute("user", new User());
	        model.addAttribute("roles", new Role[]{
	                Role.PRINCIPAL,
	                Role.COORDINATOR,
	                Role.DEO
	        });

	        return "province-head/create-school-user";
	    }
	    
	 // 🔹 Save Principal / Coordinator / DEO
	    @PostMapping("/schools/{schoolId}/users/create")
	    public String createSchoolUser(
	            @PathVariable Long schoolId,
	            @RequestParam Role role,
	            @ModelAttribute User user,
	            Authentication authentication,
	            HttpServletRequest request) {

	        if (role != Role.PRINCIPAL &&
	            role != Role.COORDINATOR &&
	            role != Role.DEO) {
	            throw new RuntimeException("Invalid role");
	        }

	        School school = schoolRepo.findById(schoolId)
	                .orElseThrow(() -> new RuntimeException("School not found"));

	        user.setRole(role);
	        user.setSchool(school);
	        user.setPassword(encoder.encode(user.getPassword()));

	        userRepo.save(user);

	        // 🔥 SCHOOL USER AUDIT LOG
	        schoolUserAuditService.log(
	                "CREATE_USER",
	                user.getId(),
	                user.getEmail(),
	                user.getRole().name(),
	                authentication.getName(),
	                request.getRemoteAddr(),
	                "Province Head created school user '" +
	                        user.getEmail() +
	                        "' with role: " + role +
	                        " for school: " + school.getName()
	        );

	        return "redirect:/provincehead/dashboard";
	    }

	    
	    
	    @GetMapping("/school-users")
	    public String viewSchoolUsers(
	            @RequestParam Long schoolId,
	            Authentication authentication,
	            Model model) {

	        User loggedUser = userRepo.findByEmail(authentication.getName());

	        School school = schoolRepo.findById(schoolId).orElse(null);

	        if (school == null) {
	            return "redirect:/province-head/schools";
	        }

	        // 🔐 Province security check
	        if (!school.getProvince().getId()
	                .equals(loggedUser.getProvince().getId())) {
	            return "redirect:/province-head/schools";
	        }


	        List<User> users = userRepo.findAllBySchool(school);

	        // 🔹 Separate by Role (assuming Role is enum)
	        List<User> principals = users.stream()
	                                     .filter(u -> u.getRole() == Role.PRINCIPAL)
	                                     .toList();

	        List<User> coordinators = users.stream()
	                                       .filter(u -> u.getRole() == Role.COORDINATOR)
	                                       .toList();

	        List<User> deos = users.stream()
	                                .filter(u -> u.getRole() == Role.DEO)
	                                .toList();

	        List<User> educators = users.stream()
	                                    .filter(u -> u.getRole() == Role.EDUCATOR)
	                                    .toList();

	        
	        model.addAttribute("school", school);
	        model.addAttribute("principals", principals);
	        model.addAttribute("coordinators", coordinators);
	        model.addAttribute("deos", deos);
	        model.addAttribute("educators", educators);
	        return "province-head/view-school-users";
	    }

	    @GetMapping("/change-password")
	    public String showChangePasswordPage() {
	        return "auth/provincehead-change-password";
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
	            return "auth/provincehead-change-password";
	        }
	        
	     // 🔥 2️⃣ NEW PASSWORD MUST NOT BE SAME AS CURRENT
	        if (encoder.matches(newPassword, user.getPassword())) {
	            model.addAttribute("error",
	                    "New password cannot be the same as your current password.");
	            return "auth/provincehead-change-password";
	        }

	        // 3 Check new password match
	        if (!newPassword.equals(confirmPassword)) {
	            model.addAttribute("error", "New passwords do not match.");
	            return "auth/provincehead-change-password";
	        }

	        // 4 Password Strength Validation
	        String passwordRegex =
	                "^(?=.*[a-z])" +        // at least 1 lowercase
	                "(?=.*[A-Z])" +         // at least 1 uppercase
	                "(?=.*\\d)" +           // at least 1 digit
	                "(?=.*[@$!%*?&])" +     // at least 1 special char
	                ".{8,}$";               // minimum 8 characters

	        if (!newPassword.matches(passwordRegex)) {
	            model.addAttribute("error",
	                    "Password must be at least 8 characters long and include uppercase, lowercase, number and special character.");
	            return "auth/provincehead-change-password";
	        }

	        // 4️⃣ Save password
	        user.setPassword(encoder.encode(newPassword));
	        userRepo.save(user);
	        
	        

	        model.addAttribute("success", "Password changed successfully.");
	        
	     // 🔥 SEND EMAIL NOTIFICATION
	        emailService.sendPasswordChangeEmail(
	                user.getEmail(),
	                user.getUsername()
	        );
	        return "auth/provincehead-change-password";
	    }

}
