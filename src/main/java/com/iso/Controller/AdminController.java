package com.iso.Controller;

import com.iso.Model.*;
import com.iso.Repository.*;
import com.iso.Service.AuditService;
import com.iso.Service.ProvinceAuditService;
import com.iso.Service.SchoolAuditService;
import com.iso.Service.SchoolUserAuditService;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {

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


    public AdminController(SchoolRepository schoolRepo,
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
                           SchoolUserAuditLogRepository schoolUserAuditLogRepository
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
    }


    // Define the directory where files will be stored
    private static final String UPLOADED_FOLDER = "src/main/resources/uploads/";
    
    // 🔹 Admin Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
    	
        model.addAttribute("schools", schoolRepo.findAll());
        return "admin/dashboard";
    }
    
    @GetMapping("/audit-logs")
    public String viewAuditLogs(@RequestParam(required = false) String action,
                                Model model) {

        List<AuditLog> logs;

        if (action != null && !action.isEmpty()) {
            logs = auditlogRepo.findByActionOrderByPerformedAtDesc(action);
        } else {
            logs = auditlogRepo.findAllByOrderByPerformedAtDesc();
        }

        model.addAttribute("logs", logs);
        model.addAttribute("selectedAction", action);

        return "admin/audit-logs";
    }


    
    @GetMapping("/hq/users/create")
    public String showCreateEimctaHqUserForm(   
            Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", new Role[]{
                Role.HR,
                Role.ACCOUNT,
                Role.SALES
        });

        return "admin/eimcta/create-hq-user";
    }
    
    @PostMapping("/company-users/create")
    public String createCompanyUser(@RequestParam Role role,
    		@ModelAttribute User user,
    		Authentication authentication,
    		HttpServletRequest request,
    		Model model) {


    	model.addAttribute("user", new User());
        if (role != Role.HR &&
            role != Role.ACCOUNT &&
            role != Role.SALES) {
            throw new RuntimeException("Invalid role");
        }

    	
        // Encode password if using Spring Security
        user.setPassword(encoder.encode(user.getPassword()));

        user.setRole(role);
        userRepo.save(user);
        
        String ipAddress = request.getRemoteAddr();
        
     // 🔥 AUDIT LOG
        auditService.log(
                "CREATE_USER",
                "USER",
                user.getId(),
                authentication.getName(),
                ipAddress,
                "Created HQ user with role: " + user.getRole()
                        + ", email: " + user.getEmail()
        );

        return "redirect:/admin/hq/users/create";
    }
    
    @GetMapping("/hq-users")
    public String viewHqUsers(Model model) {

        List<Role> hqRoles = List.of(
                Role.HR,
                Role.SALES,
                Role.ACCOUNT
        );

        List<User> users =
                userRepo.findByRoleInAndDeletedFalse(hqRoles);

        model.addAttribute("users", users);

        return "admin/eimcta/hq-users";
    }
    
    @GetMapping("/hq-users/edit")
    public String showEditHqUser(
            @RequestParam Long id,
            Model model) {

        User user = userRepo.findById(id).orElse(null);

        if (user == null) {
            return "redirect:/admin/hq-users";
        }

        if (user.getRole() != Role.HR &&
            user.getRole() != Role.SALES &&
            user.getRole() != Role.ACCOUNT) {

            return "redirect:/admin/hq-users";
        }

        model.addAttribute("user", user);

        return "admin/eimcta/edit-hq-user";
    }
    
    @PostMapping("/hq-users/edit")
    public String updateHqUser(@ModelAttribute User updatedUser,
                               Authentication authentication,
                               HttpServletRequest request) {

        User existingUser =
                userRepo.findById(updatedUser.getId()).orElse(null);

        if (existingUser == null) {
            return "redirect:/admin/hq-users";
        }

        if (existingUser.getRole() == Role.HR ||
            existingUser.getRole() == Role.SALES ||
            existingUser.getRole() == Role.ACCOUNT) {

            String performedBy = authentication.getName();

            boolean roleChanged = false;
            boolean profileChanged = false;

            StringBuilder profileDetails = new StringBuilder();

            Role oldRole = existingUser.getRole();

            // 🔹 Detect Username Change
            if (!existingUser.getUsername().equals(updatedUser.getUsername())) {
                profileChanged = true;
                profileDetails.append("Username changed from ")
                        .append(existingUser.getUsername())
                        .append(" to ")
                        .append(updatedUser.getUsername())
                        .append(". ");
            }

            // 🔹 Detect Email Change
            if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
                profileChanged = true;
                profileDetails.append("Email changed from ")
                        .append(existingUser.getEmail())
                        .append(" to ")
                        .append(updatedUser.getEmail())
                        .append(". ");
            }

            // 🔹 Detect Role Change
            if ((updatedUser.getRole() == Role.HR ||
                 updatedUser.getRole() == Role.SALES ||
                 updatedUser.getRole() == Role.ACCOUNT)
                    && existingUser.getRole() != updatedUser.getRole()) {

                roleChanged = true;
            }

            // Apply updates
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());

            if (updatedUser.getRole() == Role.HR ||
                updatedUser.getRole() == Role.SALES ||
                updatedUser.getRole() == Role.ACCOUNT) {

                existingUser.setRole(updatedUser.getRole());
            }

            userRepo.save(existingUser);
            
            String ipAddress = request.getRemoteAddr();

            // 🔥 ROLE CHANGE LOG
            if (roleChanged) {
                auditService.log(
                        "ROLE_CHANGED",
                        "USER",
                        existingUser.getId(),
                        performedBy,
                        ipAddress,
                        "Role changed from " + oldRole +
                        " to " + existingUser.getRole() +
                        " for user email: " + existingUser.getEmail()
                );
            }

            // 🔥 PROFILE CHANGE LOG (Detailed)
            if (profileChanged) {
                auditService.log(
                        "EDIT_USER_PROFILE",
                        "USER",
                        existingUser.getId(),
                        performedBy,
                        ipAddress,
                        profileDetails.toString()
                );
            }
        }

        return "redirect:/admin/hq-users";
    }

    
    @GetMapping("/hq-users/toggle")
    public String toggleHqUser(@RequestParam Long id,
    		Authentication authentication,
    		HttpServletRequest request) {

        User user = userRepo.findById(id).orElse(null);

        if (user != null &&
           (user.getRole() == Role.HR ||
            user.getRole() == Role.SALES ||
            user.getRole() == Role.ACCOUNT)) {

        	 boolean newStatus = !user.isActive();
            user.setActive(!user.isActive());
            userRepo.save(user);
       
            String ipAddress = request.getRemoteAddr();
        
        auditService.log(
                newStatus ? "USER_ACTIVATED" : "USER_DEACTIVATED",
                "USER",
                user.getId(),
                authentication.getName(),
                ipAddress,
                (newStatus ? "Activated" : "Deactivated")
                        + " user with email: " + user.getEmail()
        );
        
        }


        return "redirect:/admin/hq-users";
    }

    
    @GetMapping("/hq-users/delete")
    public String deleteHqUser(
            @RequestParam Long id,
            Authentication authentication,
            HttpServletRequest request) {

        User user = userRepo.findById(id).orElse(null);
        User loggedUser = userRepo.findByEmail(authentication.getName());

        if (user == null) {
            return "redirect:/admin/hq-users";
        }

     // Prevent self delete
        if (user.getId().equals(loggedUser.getId())) {
            return "redirect:/admin/hq-users";
        }

        if (user.getRole() == Role.HR ||
            user.getRole() == Role.SALES ||
            user.getRole() == Role.ACCOUNT) {

            user.setDeleted(true);
            user.setActive(false);

            user.setDeletedBy(loggedUser);
            user.setDeletedAt(java.time.LocalDateTime.now());

            userRepo.save(user);
        }
        
        String ipAddress = request.getRemoteAddr();
        
        auditService.log(
                "DELETE_USER",
                "USER",
                user.getId(),
                authentication.getName(),
                ipAddress,
                "Soft deleted HQ user (" + user.getRole() + 
                ") with email: " + user.getEmail()
        );

        return "redirect:/admin/hq-users";
    }
    
    
    @GetMapping("/hq-users/restore")
    public String restoreHqUser(@RequestParam Long id,
    		Authentication authentication,
    		HttpServletRequest request) {

    	
        User user = userRepo.findById(id).orElse(null);

        if (user != null && user.getDeleted()) {

            user.setDeleted(false);
            user.setActive(true);

            user.setDeletedBy(null);
            user.setDeletedAt(null);

            userRepo.save(user);
        }
        
        String ipAddress = request.getRemoteAddr();
        
        auditService.log(
                "RESTORE_USER",
                "USER",
                user.getId(),
                authentication.getName(),
                ipAddress,
                "Restored HQ user (" + user.getRole() + 
                ") with email: " + user.getEmail()
        
        );


        return "redirect:/admin/hq-users/deleted";
    }

    @GetMapping("/hq-users/deleted")
    public String viewDeletedHqUsers(Model model) {

        List<Role> hqRoles = List.of(
                Role.HR,
                Role.SALES,
                Role.ACCOUNT
        );

        List<User> users =
                userRepo.findByRoleInAndDeletedTrue(hqRoles);

        model.addAttribute("users", users);

        return "admin/eimcta/deleted-hq-users";
    }



    @GetMapping("/province-audit-logs")
    public String viewProvinceAuditLogs(@RequestParam(required = false) String action,
                                Model model) {

        List<ProvinceAuditLog> logs;

        if (action != null && !action.isEmpty()) {
            logs = provinceAuditRepo.findByActionOrderByPerformedAtDesc(action);
        } else {
            logs = provinceAuditRepo.findAllByOrderByPerformedAtDesc();
        }

        model.addAttribute("logs", logs);
        model.addAttribute("selectedAction", action);

        return "admin/province-audit-logs";
    }


    
 // Show Form to create province user
    @GetMapping("/province-users/create")
    public String showCreateProvinceUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", new Role[]{
                Role.PROVINCE_HEAD,
                Role.CONSULTANT,
                Role.EDEO,
                Role.CRO
        });

        model.addAttribute("provinces", provinceRepo.findAll());
        return "admin/eimcta/create-province-user";
    }

    // Save Province User
    @PostMapping("/province-users/create")
    public String createProvinceUser(@RequestParam Role role,
            @ModelAttribute User user,
            @RequestParam Long provinceId,
            Authentication authentication,
            HttpServletRequest request,
            Model model) {

        Province province = provinceRepo.findById(provinceId).orElseThrow();
        model.addAttribute("user", new User());
        if (role != Role.PROVINCE_HEAD &&
            role != Role.CONSULTANT &&
            role != Role.EDEO &&
            role != Role.CRO) {
            throw new RuntimeException("Invalid role");
        }

    	
        // Encode password if using Spring Security
        user.setPassword(encoder.encode(user.getPassword()));

        user.setRole(role);
        user.setProvince(province);
        user.setSchool(null); // Important

        userRepo.save(user);

        String ipAddress = request.getRemoteAddr();
        
        provinceAuditService.log(
                "CREATE_USER",
                "USER",
                user.getId(),
                authentication.getName(),
                ipAddress,
                "Created Province user (" + user.getRole() +
                ") with email: " + user.getEmail()
        );

        
        return "redirect:/admin/dashboard";
    }
    
 // 🔹 View Province Level Users
    @GetMapping("/province-users")
    public String viewProvinceUsers(Model model) {

        List<Role> provinceRoles = List.of(
                Role.PROVINCE_HEAD,
                Role.CONSULTANT,
                Role.CRO,
                Role.EDEO
        );

        List<User> users = userRepo.findByRoleInAndDeletedFalse(provinceRoles);

        model.addAttribute("users", users);

        return "admin/province-users";
    }


    @GetMapping("/province-users/toggle")
    public String toggleProvinceUser(@RequestParam Long id,
                                     Authentication authentication,
                                     HttpServletRequest request) {

        User user = userRepo.findById(id).orElse(null);

        if (user == null) {
            return "redirect:/admin/province-users";
        }

        // Allow only province-level roles
        if (user.getRole() == Role.PROVINCE_HEAD ||
            user.getRole() == Role.CONSULTANT ||
            user.getRole() == Role.CRO ||
            user.getRole() == Role.EDEO) {

            boolean newStatus = !user.isActive();   // 🔥 Determine status before saving
            user.setActive(newStatus);
            userRepo.save(user);
            
            String ipAddress = request.getRemoteAddr();

            // 🔥 Audit Log
            provinceAuditService.log(
                    newStatus ? "USER_ACTIVATED" : "USER_DEACTIVATED",
                    "USER",
                    user.getId(),
                    authentication.getName(),
                    ipAddress,
                    (newStatus ? "Activated" : "Deactivated") +
                    " Province user (" + user.getRole() +
                    ") with email: " + user.getEmail()
            );
        }

        return "redirect:/admin/province-users";
    }

    
    
    @GetMapping("/province-users/edit")
    public String showEditProvinceUser(
            @RequestParam Long id,
            Model model) {

        User user = userRepo.findById(id).orElse(null);

        if (user == null) {
            return "redirect:/admin/province-users";
        }

        // Allow only province-level roles
        if (user.getRole() != Role.PROVINCE_HEAD &&
            user.getRole() != Role.CONSULTANT &&
            user.getRole() != Role.CRO &&
            user.getRole() != Role.EDEO) {

            return "redirect:/admin/province-users";
        }

        model.addAttribute("user", user);
        
        

        return "admin/edit-province-user";
    }


    @PostMapping("/province-users/edit")
    public String updateProvinceUser(@ModelAttribute User updatedUser,
                                     Authentication authentication,
                                     HttpServletRequest request) {

        User existingUser =
                userRepo.findById(updatedUser.getId()).orElse(null);

        if (existingUser == null) {
            return "redirect:/admin/province-users";
        }

        // Only allow province-level roles
        if (existingUser.getRole() == Role.PROVINCE_HEAD ||
            existingUser.getRole() == Role.CONSULTANT ||
            existingUser.getRole() == Role.CRO ||
            existingUser.getRole() == Role.EDEO) {

            String performedBy = authentication.getName();

            boolean roleChanged = false;
            boolean profileChanged = false;
            StringBuilder profileDetails = new StringBuilder();

            Role oldRole = existingUser.getRole();

            // 🔹 Detect username change
            if (!existingUser.getUsername().equals(updatedUser.getUsername())) {
                profileChanged = true;
                profileDetails.append("Username changed from ")
                        .append(existingUser.getUsername())
                        .append(" to ")
                        .append(updatedUser.getUsername())
                        .append(". ");
            }

            // 🔹 Detect email change
            if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
                profileChanged = true;
                profileDetails.append("Email changed from ")
                        .append(existingUser.getEmail())
                        .append(" to ")
                        .append(updatedUser.getEmail())
                        .append(". ");
            }

            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());

            // Only allow switching within province-level roles
            if (updatedUser.getRole() == Role.PROVINCE_HEAD ||
                updatedUser.getRole() == Role.CONSULTANT ||
                updatedUser.getRole() == Role.CRO ||
                updatedUser.getRole() == Role.EDEO) {

                if (existingUser.getRole() != updatedUser.getRole()) {
                    roleChanged = true;
                }

                existingUser.setRole(updatedUser.getRole());
            }

            userRepo.save(existingUser);
            
            String ipAddress = request.getRemoteAddr();

            // 🔥 ROLE CHANGE LOG
            if (roleChanged) {
                provinceAuditService.log(
                        "ROLE_CHANGED",
                        "USER",
                        existingUser.getId(),
                        performedBy,
                        ipAddress,
                        "Province user role changed from " + oldRole +
                        " to " + existingUser.getRole() +
                        " (Email: " + existingUser.getEmail() + ")"
                );
            }

            // 🔥 PROFILE EDIT LOG
            if (profileChanged) {
                provinceAuditService.log(
                        "EDIT_USER_PROFILE",
                        "USER",
                        existingUser.getId(),
                        performedBy,
                        ipAddress,
                        profileDetails.toString()
                );
            }
        }

        return "redirect:/admin/province-users";
    }


    
    @GetMapping("/province-users/delete")
    public String deleteProvinceUser(
            @RequestParam Long id,
            Authentication authentication,
            HttpServletRequest request) {

        User user = userRepo.findById(id).orElse(null);
        User loggedUser = userRepo.findByEmail(authentication.getName());

        if (user == null) {
            return "redirect:/admin/province-users";
        }

        // Prevent self delete
        if (user.getId().equals(loggedUser.getId())) {
            return "redirect:/admin/province-users";
        }

        if (user.getRole() == Role.PROVINCE_HEAD ||
            user.getRole() == Role.CONSULTANT ||
            user.getRole() == Role.CRO ||
            user.getRole() == Role.EDEO) {

            user.setDeleted(true);
            user.setActive(false);   // Extra safety
            user.setDeletedBy(loggedUser);
            user.setDeletedAt(LocalDateTime.now());

            userRepo.save(user);
            
            String ipAddress = request.getRemoteAddr();

            // 🔥 Audit Log
            provinceAuditService.log(
                    "DELETE_USER",
                    "USER",
                    user.getId(),
                    authentication.getName(),
                    ipAddress,
                    "Soft deleted Province user (" + user.getRole() +
                    ") with email: " + user.getEmail()
            );
        }

        return "redirect:/admin/province-users";
    }

    
    @GetMapping("/province-users/deleted")
    public String viewDeletedProvinceUsers(Model model) {

        List<Role> provinceRoles = List.of(
                Role.PROVINCE_HEAD,
                Role.CONSULTANT,
                Role.CRO,
                Role.EDEO
        );

        List<User> deletedUsers =
                userRepo.findByRoleInAndDeletedTrue(provinceRoles);

        model.addAttribute("users", deletedUsers);

        return "admin/deleted-province-users";
    }

    
    @GetMapping("/province-users/restore")
    public String restoreProvinceUser(@RequestParam Long id, 
    		Authentication authentication,
    		HttpServletRequest request) {

        User user = userRepo.findById(id).orElse(null);

        if (user != null && user.getDeleted()) {

            user.setDeleted(false);
            user.setActive(true);

            // Clear deletion metadata
            user.setDeletedBy(null);
            user.setDeletedAt(null);

            userRepo.save(user);
            
            String ipAddress = request.getRemoteAddr();

            // 🔥 Audit Log for restore
            provinceAuditService.log(
                    "RESTORE_USER",
                    "USER",
                    user.getId(),
                    authentication.getName(),
                    ipAddress,
                    "Restored Province user (" + user.getRole() +
                    ") with email: " + user.getEmail()
            );
        }

        return "redirect:/admin/province-users/deleted";
    }




    // 🔹 Show Create School Form
    @GetMapping("/schools/create")
    public String showCreateSchoolForm(Model model) {
    	model.addAttribute("school", new School());
        model.addAttribute("provinces", provinceRepo.findAll());
        return "admin/create-school";
    }
    
    

    @PostMapping("/schools/create")
    public String createSchool(
            @ModelAttribute School school,
            @RequestParam("province") Long provinceId,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            Model model,
            Authentication authentication,
            HttpServletRequest request) {

        Province province = provinceRepo.findById(provinceId).orElse(null);

        if (province == null) {
            model.addAttribute("error", "Invalid Province selected");
            model.addAttribute("provinces", provinceRepo.findAll());
            return "admin/create-school";
        }

        if (schoolRepo.existsByName(school.getName())) {
            model.addAttribute("error", "School with this name already exists!");
            model.addAttribute("provinces", provinceRepo.findAll());
            return "admin/create-school";
        }

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

        school.setProvince(province);
        school.setActive(true);
        schoolRepo.save(school);

        // 🔥 SCHOOL AUDIT LOG (Correct place)
        schoolAuditService.log(
                "CREATE_SCHOOL",
                "SCHOOL",
                school.getId(),
                authentication.getName(),
                request.getRemoteAddr(),
                "School created: " + school.getName() +
                " in province: " + province.getName() +
                " by: " + authentication.getName()
        );

        return "redirect:/admin/dashboard";
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

        return "admin/create-school-user";
    }

    // 🔹 Save Principal / Coordinator / DEO
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
                "Created school user '" + user.getEmail() +
                "' with role: " + role +
                " for school: " + school.getName()
        );

        return "redirect:/admin/dashboard";
    }

    
    @GetMapping("/schools/edit/{id}")
    public String showEditSchoolForm(@PathVariable Long id, Model model) {
        Optional<School> optionalSchool = schoolRepo.findById(id);
        if (optionalSchool.isEmpty()) {
            return "redirect:/admin/schools"; // or show an error message
        }

        School school = optionalSchool.get();
        model.addAttribute("school", school);
        model.addAttribute("provinces", provinceRepo.findAll()); // if needed
        return "admin/edit-school";
    }
    
    @PostMapping("/schools/edit/{id}")
    public String updateSchool(
            @PathVariable Long id,
            @ModelAttribute("school") School updatedSchool,
            @RequestParam("provinceId") Long provinceId,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            Authentication authentication,
            HttpServletRequest request) {

        Optional<School> optionalSchool = schoolRepo.findById(id);
        if (optionalSchool.isEmpty()) {
            return "redirect:/admin/schools";
        }

        School existingSchool = optionalSchool.get();

        // 🔍 Track changes for audit details
        StringBuilder changes = new StringBuilder();

        if (!existingSchool.getName().equals(updatedSchool.getName())) {
            changes.append("Name changed from '")
                   .append(existingSchool.getName())
                   .append("' to '")
                   .append(updatedSchool.getName())
                   .append("'. ")
                   .append(" by: " + authentication.getName());
        }

        if (!existingSchool.getEmail().equals(updatedSchool.getEmail())) {
            changes.append("Email changed. ")
            .append(existingSchool.getEmail())
            .append("' to '")
            .append(updatedSchool.getEmail())
            .append("'. ")
            .append(" by: " + authentication.getName());;
        }

        if (!existingSchool.getMobileNumber().equals(updatedSchool.getMobileNumber())) {
            changes.append("Mobile number changed. ")
            .append(existingSchool.getMobileNumber())
            .append("' to '")
            .append(updatedSchool.getMobileNumber())
            .append("'. ")
            .append(" by: " + authentication.getName());;
        }

        if (!existingSchool.getAddress().equals(updatedSchool.getAddress())) {
            changes.append("Address changed. ")
            .append(existingSchool.getAddress())
            .append("' to '")
            .append(updatedSchool.getAddress())
            .append("'. ")
            .append(" by: " + authentication.getName());
        }

        Province province = provinceRepo.findById(provinceId).orElse(null);
        if (province != null && !province.equals(existingSchool.getProvince())) {
            changes.append("Province changed from '")
                   .append(existingSchool.getProvince().getName())
                   .append("' to '")
                   .append(province.getName())
                   .append("'. ")
                   .append(" by: " + authentication.getName());
        }

        // =========================
        // Update fields
        // =========================
        existingSchool.setName(updatedSchool.getName());
        existingSchool.setAddress(updatedSchool.getAddress());
        existingSchool.setEmail(updatedSchool.getEmail());
        existingSchool.setMobileNumber(updatedSchool.getMobileNumber());
        existingSchool.setLandlineNumber(updatedSchool.getLandlineNumber());
        existingSchool.setProvince(province);
        existingSchool.setSocialMediaURL(updatedSchool.getSocialMediaURL());

        // =========================
        // Handle logo upload
        // =========================
        if (logoFile != null && !logoFile.isEmpty()) {
            String uploadDir = "uploads/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) uploadPath.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + logoFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            try {
                Files.copy(logoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }

            existingSchool.setLogo(fileName);
            changes.append("Logo updated. ")
                         .append(" by: " + authentication.getName());
        }

        schoolRepo.save(existingSchool);

        // 🔥 SCHOOL AUDIT LOG
        schoolAuditService.log(
                "EDIT_SCHOOL",
                "SCHOOL",
                existingSchool.getId(),
                authentication.getName(),
                request.getRemoteAddr(),
                changes.length() > 0
                        ? changes.toString()
                        : "School edited but no significant changes detected."
        );

        return "redirect:/admin/provinces";
    }



    
 // 🔹 Show Create Province Form
    @GetMapping("/provinces/create")
    public String showCreateProvinceForm(Model model) {
        model.addAttribute("province", new Province());
        return "admin/create-province";
    }

    // 🔹 Save Province
    @PostMapping("/provinces/create")
    public String createProvince(@ModelAttribute Province province, Model model) {

        if (provinceRepo.existsByName(province.getName())) {
            model.addAttribute("error", "Province already exists!");
            return "admin/create-province";
        }

        provinceRepo.save(province);
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/provinces")
    public String listProvinces(Model model) {

        model.addAttribute("provinces", provinceRepo.findAll());

        return "admin/province-list";
    }

    
    @GetMapping("/provinces/delete/{id}")
    public String deleteProvince(@PathVariable Long id, Model model) {

        Province province = provinceRepo.findById(id).orElse(null);

        if (province == null) {
            return "redirect:/admin/provinces";
        }

        // 🔴 Prevent deletion if schools exist
        if (province.getSchools() != null && !province.getSchools().isEmpty()) {
            model.addAttribute("provinces", provinceRepo.findAll());
            model.addAttribute("error", 
                "Cannot delete province. Schools are assigned to it.");
            return "admin/province-list";
        }

        provinceRepo.delete(province);

        return "redirect:/admin/provinces";
    }

    
    @GetMapping("/provinces/edit/{id}")
    public String showEditProvinceForm(@PathVariable Long id, Model model) {

        Province province = provinceRepo.findById(id).orElse(null);

        if (province == null) {
            return "redirect:/admin/provinces";
        }

        model.addAttribute("province", province);
        return "admin/edit-province";
    }

    
    @PostMapping("/provinces/update/{id}")
    public String updateProvince(@PathVariable Long id,
                                 @ModelAttribute Province province,
                                 Model model) {

        Province existingProvince = provinceRepo.findById(id).orElse(null);

        if (existingProvince == null) {
            return "redirect:/admin/provinces";
        }

        // Prevent duplicate name (except itself)
        Province duplicate = provinceRepo
                .findAll()
                .stream()
                .filter(p -> p.getName().equalsIgnoreCase(province.getName())
                          && !p.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (duplicate != null) {
            model.addAttribute("province", existingProvince);
            model.addAttribute("error", "Province name already exists!");
            return "admin/edit-province";
        }

        existingProvince.setName(province.getName());
        provinceRepo.save(existingProvince);

        return "redirect:/admin/provinces";
    }

    
    @GetMapping("/provinces/{id}/schools")
    public String viewSchoolsByProvince(@PathVariable Long id, Model model) {

        Province province = provinceRepo.findById(id).orElse(null);

        if (province == null) {
            return "redirect:/admin/provinces";
        }

        model.addAttribute("province", province);
        model.addAttribute("schools", province.getSchools());

        return "admin/province-schools";
    }

    
    @GetMapping("/school-audit-logs")
    public String viewSchoolAuditLogs(
            @RequestParam(required = false) String action,
            Model model) {

        List<SchoolAuditLog> logs;

        if (action != null && !action.isEmpty()) {
            logs = schoolAuditLogRepository
                    .findByActionOrderByPerformedAtDesc(action);
        } else {
            logs = schoolAuditLogRepository
                    .findAllByOrderByPerformedAtDesc();
        }

        model.addAttribute("schoolAuditLogs", logs);
        model.addAttribute("selectedAction", action);

        return "admin/school-audit-logs";
    }
    
    @GetMapping("/school-user-audit-logs")
    public String viewSchoolUserAuditLogs(
            @RequestParam(required = false) String action,
            Model model) {

        List<SchoolUserAuditLog> logs;

        if (action != null && !action.isEmpty()) {
            logs = schoolUserAuditLogRepository
                    .findByActionOrderByPerformedAtDesc(action);
        } else {
            logs = schoolUserAuditLogRepository
                    .findAllByOrderByPerformedAtDesc();
        }

        model.addAttribute("schoolUserAuditLogs", logs);
        model.addAttribute("selectedAction", action);

        return "admin/school-user-audit-logs";
    }


    
    @GetMapping("/schools/{id}/users")
    public String viewUsersBySchool(@PathVariable Long id, Model model) {

        School school = schoolRepo.findById(id).orElse(null);
        if (school == null) {
            return "redirect:/admin/provinces";
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

        return "admin/school-users";
    }

    
 // Restrict a school
    @GetMapping("/schools/restrict/{id}")
    public String restrictSchool(@PathVariable Long id,
                                 Authentication authentication,
                                 HttpServletRequest request) {

        School school = schoolRepo.findById(id).orElse(null);

        if (school != null) {
            school.setActive(false);
            schoolRepo.save(school);

            // 🔥 SCHOOL AUDIT LOG
            schoolAuditService.log(
                    "DEACTIVATE_SCHOOL",
                    "SCHOOL",
                    school.getId(),
                    authentication.getName(),
                    request.getRemoteAddr(),
                    "School restricted: " + school.getName() +
                    " (Province: " + school.getProvince().getName() + ")" +
                    " by: " + authentication.getName()
            );
        }

        return "redirect:/admin/provinces";
    }

// Activate Schools
    @GetMapping("/schools/enable/{id}")
    public String enableSchool(@PathVariable Long id,
                               Authentication authentication,
                               HttpServletRequest request) {

        School school = schoolRepo.findById(id).orElse(null);

        if (school != null) {
            school.setActive(true);
            schoolRepo.save(school);

            // 🔥 SCHOOL AUDIT LOG
            schoolAuditService.log(
                    "ACTIVATE_SCHOOL",
                    "SCHOOL",
                    school.getId(),
                    authentication.getName(),
                    request.getRemoteAddr(),
                    "School enabled: " + school.getName() +
                    " (Province: " + school.getProvince().getName() + ")"
                    + " by: " + authentication.getName()
            );
        }

        return "redirect:/admin/provinces";
    }


    
}
