package com.iso.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iso.Model.AcademicYear;
import com.iso.Model.EducatorAssignment;
import com.iso.Model.User;
import com.iso.Repository.AcademicYearRepository;
import com.iso.Repository.EducatorAssignmentRepository;
import com.iso.Repository.ProvinceRepository;
import com.iso.Repository.SchoolRepository;
import com.iso.Repository.UserRepository;

@Controller
@RequestMapping("/educator")
@PreAuthorize("hasRole('EDUCATOR')")
public class EducatorController {

	 private final SchoolRepository schoolRepo;
	 private final UserRepository userRepo;
	    private final ProvinceRepository provinceRepo;
	    private final PasswordEncoder encoder;
	    private final AcademicYearRepository academicYearRepository;
	    private final EducatorAssignmentRepository educatorAssignmentRepository;
	    
	    public EducatorController(SchoolRepository schoolRepo,
                UserRepository userRepo,
                PasswordEncoder encoder,
                ProvinceRepository provinceRepo,
                AcademicYearRepository academicYearRepository,
                EducatorAssignmentRepository educatorAssignmentRepository) {
           this.schoolRepo = schoolRepo;
           this.userRepo = userRepo;
           this.encoder = encoder;
           this.provinceRepo = provinceRepo;
           this.educatorAssignmentRepository = educatorAssignmentRepository;
           this.academicYearRepository = academicYearRepository;
          }
	    
	    @GetMapping("/dashboard")
	    public String hrDashboard() {
	        return "educator/dashboard";
	    }
	    
	    @GetMapping("/educators/{educatorId}/subjects")
	    public String viewEducatorSubjects(@PathVariable Long educatorId,
	                                       Model model,
	                                       Principal principal) {

	        // 1️⃣ Get logged-in principal (or use educator principal for security check)
	        User principalUser = userRepo.findByEmail(principal.getName());

	        // 2️⃣ Get active academic year
	        AcademicYear activeYear = academicYearRepository
	                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
	                .orElseThrow(() -> new RuntimeException("No active academic year"));

	        // 3️⃣ Fetch all assignments for this educator, active year, not restricted
	        List<EducatorAssignment> assignments =
	                educatorAssignmentRepository
	                .findByEducatorIdAndSectionClassRoomAcademicYearIdAndRestrictedFalse(
	                        educatorId, activeYear.getId()
	                );

	        // 4️⃣ Pass data to view
	        model.addAttribute("assignments", assignments);
	        model.addAttribute("educator", userRepo.findById(educatorId)
	                .orElseThrow(() -> new RuntimeException("Educator not found")));

	        return "principal/view-educator-subjects";
	    }
	    
	    
}
