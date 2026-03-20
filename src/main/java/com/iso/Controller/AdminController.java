package com.iso.Controller;

import com.iso.Model.*;
import com.iso.Repository.*;
import com.iso.Service.AuditService;
import com.iso.Service.PrincipalAttachmentService;
import com.iso.Service.ProvinceAuditService;
import com.iso.Service.SchoolAuditService;
import com.iso.Service.SchoolUserAuditService;
import com.iso.Service.SchoolUserEditAuditLogService;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
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
    private final OrganizationRegistrationRepository organizationRegistrationRepository;
    private final RegistrationStatusAuditLogRepository registrationStatusAuditLogRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final ContactStatusAuditLogRepository contactStatusAuditLogRepository;
    private final PrincipalRepository principalRepository;
    private final PrincipalAttachmentService attachmentService;
    private final SchoolUserEditAuditLogService schoolusereditauditLogService;


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
                           SchoolUserAuditLogRepository schoolUserAuditLogRepository,
                           OrganizationRegistrationRepository organizationRegistrationRepository,
                           RegistrationStatusAuditLogRepository registrationStatusAuditLogRepository,
                           ContactMessageRepository contactMessageRepository,
                           ContactStatusAuditLogRepository contactStatusAuditLogRepository,
                           PrincipalRepository principalRepository,
                           PrincipalAttachmentService attachmentService,
                           SchoolUserEditAuditLogService schoolusereditauditLogService
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
        this.organizationRegistrationRepository = organizationRegistrationRepository;
        this.registrationStatusAuditLogRepository = registrationStatusAuditLogRepository;
        this.contactMessageRepository = contactMessageRepository;
        this.contactStatusAuditLogRepository = contactStatusAuditLogRepository;
        this.principalRepository = principalRepository;
        this.attachmentService = attachmentService;
        this.schoolusereditauditLogService = schoolusereditauditLogService;
    }


    // Define the directory where files will be stored
    private static final String UPLOADED_FOLDER = "src/main/resources/uploads/";
    
    // 🔹 Admin Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
    	model.addAttribute("title", "Dashboard");

       

        model.addAttribute("schools", schoolRepo.findAll());
        return "admin/dashboard";
    }
    
 // ===============================
    // View All Registrations
    // ===============================
    @GetMapping("/registrations")
    public String viewAllRegistrations(
            @RequestParam(required = false) RegistrationStatus status,
            Model model) {

        List<OrganizationRegistration> registrations;

        if (status != null) {
            registrations = organizationRegistrationRepository
                    .findByStatusOrderByPerformedAtDesc(status);
        } else {
            registrations = organizationRegistrationRepository
                    .findAllByOrderByPerformedAtDesc();
        }

        model.addAttribute("registrations", registrations);
        model.addAttribute("selectedAction", status);
        model.addAttribute("statuses", RegistrationStatus.values());

        return "admin/registrations";
    }
    
    
    // ===============================
    // Update Status
    // ===============================
    @PostMapping("/registrations/update-status/{id}")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam RegistrationStatus status,
            Principal principal,
            HttpServletRequest request) {

        OrganizationRegistration registration =
                organizationRegistrationRepository.findById(id).orElseThrow();

        RegistrationStatus oldStatus = registration.getStatus();

        String ipAddress = request.getRemoteAddr();
        
        // Only log if status actually changes
        if (!oldStatus.equals(status)) {

            registration.setStatus(status);
            organizationRegistrationRepository.save(registration);

            // 🔹 Create log entry
            RegistrationStatusAuditLog log = new RegistrationStatusAuditLog();
            log.setRegistrationId(registration.getId());
            log.setChangedBy(principal.getName()); // logged-in admin username
            log.setFromStatus(oldStatus);
            log.setToStatus(status);
            log.setPerformedAt(registration.getPerformedAt());
            log.setIpAddress(ipAddress);

            registrationStatusAuditLogRepository.save(log);
        }
        
       
        
        
        return "redirect:/admin/registrations";
    }
    
    
    
    
    @GetMapping("/registrations/logs")
    public String viewLogs(Model model,
    		@RequestParam(required = false) RegistrationStatus toStatus
    		) {
    	 List<RegistrationStatusAuditLog> logs;

    	    if (toStatus != null) {
    	        logs = registrationStatusAuditLogRepository
    	                .findByToStatusOrderByChangedAtDesc(toStatus);
    	    } else {
    	        logs = registrationStatusAuditLogRepository
    	                .findAllByOrderByChangedAtDesc();
    	    }

    	    model.addAttribute("logs", logs);
    	    model.addAttribute("selectedStatus", toStatus);
    	    model.addAttribute("statuses", RegistrationStatus.values());
      return "admin/registration-logs";
    }
    
    @GetMapping("/contacts")
    public String viewAllContacts(
            @RequestParam(required = false) ContactStatus status,
            Model model) {

        List<ContactMessage> contacts;

        if (status != null) {
            contacts = contactMessageRepository
                    .findByStatusOrderByPerformedAtDesc(status);
        } else {
        	contacts = contactMessageRepository
                    .findAllByOrderByPerformedAtDesc();
        }

        model.addAttribute("contacts", contacts);
        model.addAttribute("selectedAction", status);
        model.addAttribute("statuses", ContactStatus.values());

        return "admin/contacts";
    }
    
    @PostMapping("/contacts/update-status/{id}")
    public String updateContactStatus(
            @PathVariable Long id,
            @RequestParam ContactStatus status,
            Principal principal,
            HttpServletRequest request) {

        ContactMessage contact =
                contactMessageRepository.findById(id).orElseThrow();

        ContactStatus oldStatus = contact.getStatus();

        String ipAddress = request.getRemoteAddr();
        
        // Only log if status actually changes
        if (!oldStatus.equals(status)) {

            contact.setStatus(status);
            contactMessageRepository.save(contact);

            // 🔹 Create log entry
           ContactStatusAuditLog log = new ContactStatusAuditLog();
            log.setContactId(contact.getId());
            log.setChangedBy(principal.getName()); // logged-in admin username
            log.setFromStatus(oldStatus);
            log.setToStatus(status);
            log.setPerformedAt(contact.getPerformedAt());
            log.setIpAddress(ipAddress);

            contactStatusAuditLogRepository.save(log);
        }
        
       
        
        
        return "redirect:/admin/contacts";
    }
    
    @GetMapping("/contacts/logs")
    public String viewContactLogs(Model model,
    		@RequestParam(required = false) ContactStatus toStatus
    		) {
    	 List<ContactStatusAuditLog> logs;

    	    if (toStatus != null) {
    	        logs = contactStatusAuditLogRepository
    	                .findByToStatusOrderByChangedAtDesc(toStatus);
    	    } else {
    	        logs = contactStatusAuditLogRepository
    	                .findAllByOrderByChangedAtDesc();
    	    }

    	    model.addAttribute("logs", logs);
    	    model.addAttribute("selectedStatus", toStatus);
    	    model.addAttribute("statuses", ContactStatus.values());
      return "admin/contact-logs";
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

        return "redirect:/admin/hq-users";
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

        // 1️⃣ Prepare User
        user.setRole(role);
        user.setSchool(school);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setActive(true);
        user.setDeleted(false);

        userRepo.save(user);

        // 2️⃣ Create Role-Specific Profile
        switch (role) {

            case PRINCIPAL -> {
                com.iso.Model.Principal principal = new com.iso.Model.Principal();
                principal.setUser(user);
                principal.setSchoolName(school.getName());
                
             // 🔥 IMPORTANT PART (INITIALIZE PROGRESS)
                principal.setCurrentSection(1);      // Section 1 open
                principal.setCompletedSection(0);    // Nothing completed
                principal.setFormCompleted(false);   // Not finished
                
                principalRepository.save(principal);
            }

            
            default -> throw new IllegalStateException("Unexpected role: " + role);
        }

        // 3️⃣ SCHOOL USER AUDIT LOG
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
    
    // Progress page
    @GetMapping("/principal/{userId}/progress")
    public String showProgress(@PathVariable Long userId, Model model) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        model.addAttribute("principal", principal);
        return "admin/principal/progress";
    }

    
    @GetMapping("/principal/{userId}/section/1")
    public String loadSection1(@PathVariable Long userId, Model model) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        // 🔐 Security
        if (principal.isFormCompleted() ||
            principal.getCurrentSection() != 1) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        // ✅ VERY IMPORTANT (Initialize Embedded Objects)
        if (principal.getPersonalInfo() == null) {
            principal.setPersonalInfo(new PrincipalPersonalInfo());
        }

        if (principal.getFamilyInfo() == null) {
            principal.setFamilyInfo(new PrincipalFamilyInfo());
        }

        model.addAttribute("principal", principal);
        return "admin/principal/section1";
    }
    
    @PostMapping("/principal/{userId}/section/1")
    public String submitSection1(@PathVariable Long userId,
                                 @ModelAttribute com.iso.Model.Principal formPrincipal) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        if (principal.getCurrentSection() != 1) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        // ✅ SAVE EMBEDDED OBJECTS
        principal.setPersonalInfo(formPrincipal.getPersonalInfo());
        principal.setFamilyInfo(formPrincipal.getFamilyInfo());

        // ✅ UPDATE PROGRESS
        principal.setCompletedSection(1);
        principal.setCurrentSection(2);

        principalRepository.save(principal);

        return "redirect:/admin/principal/" + userId + "/progress";
    }
    
    @GetMapping("/principal/{userId}/section/1/edit")
    public String editSection1(@PathVariable Long userId, Model model) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        model.addAttribute("principal", principal);
        model.addAttribute("editMode", true);
        
        

        return "admin/principal/section1-edit";
    }
    
    
    
    @PostMapping("/principal/{userId}/section/1/edit")
    public String updateSection1(@PathVariable Long userId,
                                 @ModelAttribute("principal") com.iso.Model.Principal formPrincipal,
                                 Principal loggedUser,
                                 HttpServletRequest request) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        User performer = userRepo.findByEmail(loggedUser.getName());
        User targetUser = principal.getUser();
        String details = schoolusereditauditLogService.generateSection1ChangeDetails(principal, formPrincipal);
        
        // Update Embedded Personal Info
        principal.setPersonalInfo(formPrincipal.getPersonalInfo());

        // Update Embedded Family Info
        principal.setFamilyInfo(formPrincipal.getFamilyInfo());

        // Reset workflow
        principal.setFormCompleted(false);
        principal.setPrincipalApproved(false);
        principal.setCurrentSection(7);

        principalRepository.save(principal);
        
        // Log audit
        if (!details.isBlank()) {
        	schoolusereditauditLogService.log(
                    performer.getEmail(),
                    targetUser.getEmail(),
                    "PRINCIPAL",
                    "EDIT_SECTION_1",
                    details,
                    request.getRemoteAddr()
            );
        }


        return "redirect:/admin/principal/" + userId + "/progress";
    }
    
    
    
    
    @GetMapping("/principal/{userId}/section/2")
    public String loadSection2(@PathVariable Long userId, Model model) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        // 🔐 Security: prevent manual URL access
        if (principal.isFormCompleted() ||
            principal.getCurrentSection() != 2) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        // ✅ VERY IMPORTANT: Initialize List if null
        if (principal.getEducations() == null || principal.getEducations().isEmpty()) {
            principal.setEducations(new ArrayList<>());
            principal.getEducations().add(new PrincipalEducation()); // At least 1 row
        }

        model.addAttribute("principal", principal);
        return "admin/principal/section2"; // Your Thymeleaf template for Section 2
    }
    
    
    @PostMapping("/principal/{userId}/section/2")
    public String submitSection2(
            @PathVariable Long userId,
            @ModelAttribute("principal") com.iso.Model.Principal formPrincipal) {

        // 🔹 Fetch the principal
        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        // 🔐 Security: Only allow if current section is 2
        if (principal.getCurrentSection() != 2) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        // 🔹 Clear old education records safely
        if (principal.getEducations() != null) {
            principal.getEducations().clear();
        } else {
            principal.setEducations(new ArrayList<>());
        }

        // 🔹 Add new education records from form
        if (formPrincipal.getEducations() != null) {
            for (PrincipalEducation edu : formPrincipal.getEducations()) {

                // Ignore empty rows
                if (edu.getHighestQualification() != null &&
                    !edu.getHighestQualification().isBlank()) {

                    edu.setPrincipal(principal); // IMPORTANT: link to principal
                    principal.getEducations().add(edu);
                }
            }
        }

        // 🔹 Update section progress
        principal.setCompletedSection(2);
        principal.setCurrentSection(3);

        // 🔹 Save principal with updated education info
        principalRepository.save(principal);

        return "redirect:/admin/principal/" + userId + "/progress";
    }
    
    @GetMapping("/principal/{userId}/section/2/edit")
    public String EditSection2(@PathVariable Long userId, Model model) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        

        // ✅ VERY IMPORTANT: Initialize List if null
        if (principal.getEducations() == null || principal.getEducations().isEmpty()) {
            principal.setEducations(new ArrayList<>());
            principal.getEducations().add(new PrincipalEducation()); // At least 1 row
        }

        model.addAttribute("principal", principal);
        model.addAttribute("editMode", true);
        return "admin/principal/section2-edit"; // Your Thymeleaf template for Section 2
    }
    
    
    @PostMapping("/principal/{userId}/section/2/edit")
    public String submitEditSection2(
            @PathVariable Long userId,
            @ModelAttribute("principal") com.iso.Model.Principal formPrincipal,
            Principal loggedUser,
            HttpServletRequest request) {

        // 🔹 Fetch the principal
        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        User performer = userRepo.findByEmail(loggedUser.getName());
        User targetUser = principal.getUser();
        String details = schoolusereditauditLogService.generateSection2ChangeDetails(
                principal.getEducations(),
                formPrincipal.getEducations()
        );
        
        List<PrincipalEducation> oldList = principal.getEducations();
        List<PrincipalEducation> newList = formPrincipal.getEducations();

        // Remove deleted educations
        oldList.removeIf(oldEdu ->
            newList.stream().noneMatch(newEdu ->
                newEdu.getId() != null && newEdu.getId().equals(oldEdu.getId())
            )
        );

        // Attach principal to new rows
        for (PrincipalEducation edu : newList) {
            edu.setPrincipal(principal);
        }
        
        
        
        // 🔹 Clear old education records safely
        if (principal.getEducations() != null) {
            principal.getEducations().clear();
        } else {
            principal.setEducations(new ArrayList<>());
        }

        // 🔹 Add new education records from form
        if (formPrincipal.getEducations() != null) {
            for (PrincipalEducation edu : formPrincipal.getEducations()) {

                // Ignore empty rows
                if (edu.getHighestQualification() != null &&
                    !edu.getHighestQualification().isBlank()) {

                    edu.setPrincipal(principal); // IMPORTANT: link to principal
                    principal.getEducations().add(edu);
                }
            }
        }

        // 🔹 Update section progress
     // Reset workflow
        principal.setFormCompleted(false);
        principal.setPrincipalApproved(false);
        principal.setCurrentSection(7);

        // 🔹 Save principal with updated education info
        principalRepository.save(principal);
        
     // Log audit
        if (!details.isBlank()) {
        	schoolusereditauditLogService.log(
                    performer.getEmail(),
                    targetUser.getEmail(),
                    "PRINCIPAL",
                    "EDIT_SECTION_2",
                    details,
                    request.getRemoteAddr()
            );
        }

        return "redirect:/admin/principal/" + userId + "/progress";
    }
    
    
 // 🔹 Load Section 3 (Work Experience)
    @GetMapping("/principal/{userId}/section/3")
    public String loadSection3(@PathVariable Long userId, Model model) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        // 🔐 Security: only current section can be accessed
        if (principal.isFormCompleted() || principal.getCurrentSection() != 3) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        // ✅ Initialize list if null
        if (principal.getWorkExperiences() == null) {
            principal.setWorkExperiences(new ArrayList<>());
        }

        model.addAttribute("principal", principal);
        return "admin/principal/section3";
    }

    // 🔹 Submit Section 3 (Work Experience)
    @PostMapping("/principal/{userId}/section/3")
    public String submitSection3(@PathVariable Long userId,
                                 @ModelAttribute("principal") com.iso.Model.Principal formPrincipal) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        if (principal.getCurrentSection() != 3) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        // Clear old work experiences
        principal.getWorkExperiences().clear();

        if (formPrincipal.getWorkExperiences() != null) {
            for (PrincipalWorkExperience exp : formPrincipal.getWorkExperiences()) {
                if (exp.getGrade() != null && !exp.getGrade().isBlank()) {
                    exp.setPrincipal(principal); // VERY IMPORTANT
                    principal.getWorkExperiences().add(exp);
                }
            }
        }

        // Update progress
        principal.setCompletedSection(3);
        principal.setCurrentSection(4);
        principalRepository.save(principal);

        return "redirect:/admin/principal/" + userId + "/progress";
    }
    
 // 🔹 Load Edit Section 3 (Work Experience)
    @GetMapping("/principal/{userId}/section/3/edit")
    public String loadEditSection3(@PathVariable Long userId, Model model) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        

        // ✅ Initialize list if null
        if (principal.getWorkExperiences() == null) {
            principal.setWorkExperiences(new ArrayList<>());
        }

        model.addAttribute("principal", principal);
        return "admin/principal/section3-edit";
    }

    // 🔹 Submit Updated Section 3 (Work Experience)
    @PostMapping("/principal/{userId}/section/3/edit")
    public String submitEditSection3(@PathVariable Long userId,
                                     @ModelAttribute("principal") com.iso.Model.Principal formPrincipal) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        List<PrincipalWorkExperience> newList = new ArrayList<>();

        if (formPrincipal.getWorkExperiences() != null) {

            for (PrincipalWorkExperience exp : formPrincipal.getWorkExperiences()) {

                if (exp.getGrade() != null && !exp.getGrade().isBlank()) {

                    exp.setPrincipal(principal);

                    if (exp.getTools() == null) {
                        exp.setTools(new ArrayList<>());
                    }

                    newList.add(exp);
                }
            }
        }

        principal.getWorkExperiences().clear();
        principal.getWorkExperiences().addAll(newList);

        // 🔹 Update section progress
        // Reset workflow
           principal.setFormCompleted(false);
           principal.setPrincipalApproved(false);
           principal.setCurrentSection(7);

        principalRepository.save(principal);

        return "redirect:/admin/principal/" + userId + "/progress";
    }
    
    
    @GetMapping("/principal/{userId}/section/4")
    public String loadSection4(@PathVariable Long userId, Model model) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        // 🔐 Security
        if (principal.isFormCompleted() ||
            principal.getCurrentSection() != 4) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        if (principal.getCurrentJob() == null) {
            PrincipalCurrentJob job = new PrincipalCurrentJob();
            job.setPrincipal(principal);
            principal.setCurrentJob(job);
        }

        if (principal.getCurrentJob().getJobDetails() == null ||
            principal.getCurrentJob().getJobDetails().isEmpty()) {

            principal.getCurrentJob().getJobDetails().add(new PrincipalCurrentJobDetail());
        }

        model.addAttribute("principal", principal);

        return "admin/principal/section4";
    }
    
    
    @PostMapping("/principal/{userId}/section/4")
    public String submitSection4(@PathVariable Long userId,
                                 @ModelAttribute("principal") com.iso.Model.Principal formPrincipal) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        if (principal.getCurrentSection() != 4) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        PrincipalCurrentJob formJob = formPrincipal.getCurrentJob();

        PrincipalCurrentJob job = principal.getCurrentJob();
        if (job == null) {
            job = new PrincipalCurrentJob();
            job.setPrincipal(principal);
        }

        // Save single fields
        job.setWorkingHours(formJob.getWorkingHours());

        // Clear old details
        job.getJobDetails().clear();

        if (formJob.getJobDetails() != null) {
            for (PrincipalCurrentJobDetail detail : formJob.getJobDetails()) {

                if (detail.getPeriodNumber() != null &&
                    !detail.getPeriodNumber().isBlank()) {

                    detail.setCurrentJob(job);
                    job.getJobDetails().add(detail);
                }
            }
        }

        principal.setCurrentJob(job);

        principal.setCompletedSection(4);
        principal.setCurrentSection(5);

        principalRepository.save(principal);

        return "redirect:/admin/principal/" + userId + "/progress";
    }
    
    
    @GetMapping("/principal/{userId}/section/5")
    public String loadSection5(@PathVariable Long userId, Model model) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        // 🔐 Security
        if (principal.isFormCompleted() ||
            principal.getCurrentSection() != 5) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        if (principal.getCapabilityRating() == null) {
            PrincipalCapabilityRating rating = new PrincipalCapabilityRating();
            rating.setPrincipal(principal);
            principal.setCapabilityRating(rating);
        }

        model.addAttribute("principal", principal);

        return "admin/principal/section5";
    }
     
    
    @PostMapping("/principal/{userId}/section/5")
    public String submitSection5(@PathVariable Long userId,
                                 @ModelAttribute("principal") com.iso.Model.Principal formPrincipal) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        if (principal.getCurrentSection() != 5) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        PrincipalCapabilityRating formRating = formPrincipal.getCapabilityRating();

        PrincipalCapabilityRating rating = principal.getCapabilityRating();
        if (rating == null) {
            rating = new PrincipalCapabilityRating();
            rating.setPrincipal(principal);
        }

        // Copy values
        rating.setVoiceGesture(formRating.getVoiceGesture());
        rating.setPhonetics(formRating.getPhonetics());
        rating.setFluencyNepali(formRating.getFluencyNepali());
        rating.setFluencyEnglish(formRating.getFluencyEnglish());
        rating.setClarity(formRating.getClarity());
        rating.setModulation(formRating.getModulation());
        rating.setIdiolectUse(formRating.getIdiolectUse());
        rating.setHearingCapacity(formRating.getHearingCapacity());
        rating.setHandwriting(formRating.getHandwriting());
        rating.setGrammar(formRating.getGrammar());
        rating.setSentenceStructure(formRating.getSentenceStructure());
        rating.setWiritngSpeed(formRating.getWiritngSpeed());
        rating.setVocabularyUsage(formRating.getVocabularyUsage());
        rating.setSentenceFormation(formRating.getSentenceFormation());
        rating.setVisibility(formRating.getVisibility());
        rating.setListening(formRating.getListening());
        rating.setTemperament(formRating.getTemperament());
        rating.setLeadershipSkills(formRating.getLeadershipSkills());
        rating.setTeamwork(formRating.getTeamwork());
        rating.setPresentationSkills(formRating.getPresentationSkills());
        rating.setCommunicationSkills(formRating.getCommunicationSkills());
        rating.setNegotiationSkills(formRating.getNegotiationSkills());
        rating.setProblemSolvingSkills(formRating.getProblemSolvingSkills());
        rating.setMotivationalSkills(formRating.getMotivationalSkills());
        rating.setPunctuality(formRating.getPunctuality());
        rating.setClassroomTimemanagement(formRating.getClassroomTimemanagement());
        rating.setOpeningclosingofClass(formRating.getOpeningclosingofClass());
        rating.setLearnersagCapacity(formRating.getLearnersagCapacity());
        rating.setUsingFormattiveassessment(formRating.getUsingFormattiveassessment());
        rating.setUseworksheetHw(formRating.getUseworksheetHw());
        rating.setUsesocialmediaLearners(formRating.getUsesocialmediaLearners());
        rating.setUseLearningaids(formRating.getUseLearningaids());
        rating.setScoreCw(formRating.getScoreCw());
        rating.setScoreHw(formRating.getScoreHw());
        rating.setScoreUnitassessment(formRating.getScoreUnitassessment());
        rating.setUseTeachingaids(formRating.getUseTeachingaids());
        rating.setKnowledge(formRating.getKnowledge());
        rating.setUnderstanding(formRating.getUnderstanding());
        rating.setApplication(formRating.getApplication());
        rating.setHigherAbility(formRating.getHigherAbility());
        rating.setSocialmediaHandling(formRating.getSocialmediaHandling());
        rating.setRecordKeeping(formRating.getRecordKeeping());
        rating.setImplementationCorporatepolicies(formRating.getImplementationCorporatepolicies());
        rating.setConsumingRestrictedelements(formRating.getConsumingRestrictedelements());
        rating.setPersonalHygiene(formRating.getPersonalHygiene());

        principal.setCapabilityRating(rating);

        // ✅ FINAL STEP
        principal.setCompletedSection(5);
        principal.setCurrentSection(6);

        principalRepository.save(principal);

        return "redirect:/admin/principal/" + userId + "/progress";
    }
    
    
    @GetMapping("/principal/{userId}/section/6")
    public String loadSection6(@PathVariable Long userId, Model model) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        // 🔐 Security Check
        if (principal.isFormCompleted() ||
            principal.getCurrentSection() != 6) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        // Initialize list if null (important for Thymeleaf)
        if (principal.getAttachments() == null) {
            principal.setAttachments(new ArrayList<>());
        }

        model.addAttribute("principal", principal);

        return "admin/principal/section6";
    }
       

    @PostMapping("/principal/{userId}/section/6")
    public String saveSection6(
            @PathVariable Long userId,

            @RequestParam("photoFile") MultipartFile photoFile,
            @RequestParam("citizenshipFile") MultipartFile citizenshipFile,
            @RequestParam("cvFile") MultipartFile cvFile,
            @RequestParam("educationCertificates") MultipartFile[] educationCertificates,
            @RequestParam("experienceLetters") MultipartFile[] experienceLetters,
            @RequestParam("trainingFiles") MultipartFile[] trainingFiles,
            @RequestParam("swotFiles") MultipartFile[] swotFiles,
            @RequestParam("recommendationFile") MultipartFile recommendationFile

    ) throws IOException {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        String uploadDir = "uploads/principals/" + userId + "/";

        // Use service to save files
        attachmentService.saveSingleFile(photoFile, principal, AttachmentType.PHOTO, uploadDir);
        attachmentService.saveSingleFile(citizenshipFile, principal, AttachmentType.CITIZENSHIP, uploadDir);
        attachmentService.saveSingleFile(cvFile, principal, AttachmentType.CV, uploadDir);
        attachmentService.saveSingleFile(recommendationFile, principal, AttachmentType.RECOMMENDATION_LETTER, uploadDir);

        attachmentService.saveMultipleFiles(educationCertificates, principal, AttachmentType.EDUCATIONAL_CERTIFICATE, uploadDir);
        attachmentService.saveMultipleFiles(experienceLetters, principal, AttachmentType.EXPERIENCE_LETTER, uploadDir);
        attachmentService.saveMultipleFiles(trainingFiles, principal, AttachmentType.TRAINING_CERTIFICATE, uploadDir);
        attachmentService.saveMultipleFiles(swotFiles, principal, AttachmentType.SWOT_RESEARCH, uploadDir);

        // Move to next section
     // ✅ FINAL STEP
        principal.setCompletedSection(6);
        principal.setCurrentSection(7);
        principalRepository.save(principal);

        return "redirect:/admin/principal/" + userId + "/section/7";
    }
    
    @GetMapping("/principal/{userId}/section/7")
    public String loadSection7(@PathVariable Long userId, Model model) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        // Security
        if (principal.isFormCompleted() || principal.getCurrentSection() != 7) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        // Prevent null errors
        if (principal.getCurrentJob() == null) {
            principal.setCurrentJob(new PrincipalCurrentJob());
        }
        
        
        if (principal.getCapabilityRating() == null) {
            principal.setCapabilityRating(new PrincipalCapabilityRating());
        }

        model.addAttribute("principal", principal);

        return "admin/principal/section7";
    }
    
    @PostMapping("/principal/{userId}/section/7")
    public String submitSection7(@PathVariable Long userId) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        if (principal.getCurrentSection() != 7) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        // Admin finished filling form
        principal.setCompletedSection(7);

        // Waiting for principal approval
        principal.setCurrentSection(0);

        principalRepository.save(principal);

        return "redirect:/admin/principal/" + userId + "/progress";
    }
    
    @PostMapping("/principal/{userId}/approve-form")
    public String approvePrincipalForm(@PathVariable Long userId) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        principal.setFormCompleted(true);

        principalRepository.save(principal);

        return "redirect:/admin/principal/" + userId + "/progress";
    }
    
    @GetMapping("/principal/{userId}/section/{sectionNumber}")
    public String loadSection(@PathVariable Long userId,
                              @PathVariable int sectionNumber,
                              Model model) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        // 🔐 Security check
        if (principal.isFormCompleted() ||
            sectionNumber != principal.getCurrentSection()) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        // ✅ Initialize embedded objects for section 1
        if (sectionNumber == 1) {

            if (principal.getPersonalInfo() == null) {
                principal.setPersonalInfo(new PrincipalPersonalInfo());
            }

            if (principal.getFamilyInfo() == null) {
                principal.setFamilyInfo(new PrincipalFamilyInfo());
            }

            model.addAttribute("principal", principal);
            return "admin/principal/section1";
        }

        return "redirect:/admin/principal/" + userId + "/progress";
    }
    
    @PostMapping("/principal/{userId}/section/{sectionNumber}")
    public String submitSection(
            @PathVariable Long userId,
            @PathVariable int sectionNumber) {

        com.iso.Model.Principal principal = principalRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        // 🔐 SECURITY CHECK
        if (sectionNumber != principal.getCurrentSection()) {
            return "redirect:/admin/principal/" + userId + "/progress";
        }

        // Mark completed
        principal.setCompletedSection(sectionNumber);

        if (sectionNumber < 7) {
            principal.setCurrentSection(sectionNumber + 1);
        } else {
            principal.setFormCompleted(true);
        }

        principalRepository.save(principal);

        return "redirect:/admin/principal/" + userId + "/progress";
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
        
        if (!existingSchool.getLandlineNumber().equals(updatedSchool.getLandlineNumber())) {
            changes.append("Landline Number changed. ")
            .append(existingSchool.getLandlineNumber())
            .append("' to '")
            .append(updatedSchool.getLandlineNumber())
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
