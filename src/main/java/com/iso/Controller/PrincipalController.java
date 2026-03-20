package com.iso.Controller;

import com.iso.Model.*;
import com.iso.Model.dto.ClassRoomDTO;
import com.iso.Model.dto.LearnerEnrollmentView;
import com.iso.Model.dto.LearnerPromotionDTO;
import com.iso.Model.dto.LearnerSection2DTO;
import com.iso.Model.dto.SectionDTO;
import com.iso.Model.dto.SubjectDTO;
import com.iso.Model.dto.SubjectsDTO;
import com.iso.Model.dto.UnitDTO;
import com.iso.Repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/principal")
@PreAuthorize("hasRole('PRINCIPAL')")
public class PrincipalController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final PrincipalRepository principalRepository;
    private final ClassRoomRepository classRoomRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;
    private final UnitRepository unitRepository;
    private final ChapterRepository chapterRepository;
    private final EducatorAssignmentRepository educatorAssignmentRepository;
    private final LearnerEnrollmentRepository learnerEnrollmentRepository;
    private final AcademicYearRepository academicYearRepository;
    private final LearnerRepository learnerRepository;
    private final LearnerBasicDetailsRepository learnerBasicDetailsRepository;
    private final LearnerPastSchoolPerformanceRepository learnerPastSchoolPerformanceRepository;
    private final LearnerEntranceScoreRepository learnerEntranceScoreRepository;
    private final LearnerRubricsEvaluationRepository learnerRubricsEvaluationRepository;


    public PrincipalController(UserRepository userRepo,
                               PasswordEncoder encoder,
                               PrincipalRepository principalRepository,
                               ClassRoomRepository classRoomRepository,
                               SectionRepository sectionRepository,
                               SubjectRepository subjectRepository,
                               UnitRepository unitRepository,
                               ChapterRepository chapterRepository,
                               EducatorAssignmentRepository educatorAssignmentRepository,
                               LearnerEnrollmentRepository learnerEnrollmentRepository,
                               AcademicYearRepository academicYearRepository,
                               LearnerRepository learnerRepository,
                               LearnerBasicDetailsRepository learnerBasicDetailsRepository,
                               LearnerPastSchoolPerformanceRepository learnerPastSchoolPerformanceRepository,
                               LearnerEntranceScoreRepository learnerEntranceScoreRepository,
                               LearnerRubricsEvaluationRepository learnerRubricsEvaluationRepository)
                                {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.principalRepository = principalRepository;
        this.classRoomRepository = classRoomRepository;
        this.sectionRepository = sectionRepository;
        this.subjectRepository = subjectRepository;
        this.unitRepository = unitRepository;
        this.chapterRepository = chapterRepository;
        this.educatorAssignmentRepository = educatorAssignmentRepository;
        this.learnerEnrollmentRepository = learnerEnrollmentRepository;
        this.academicYearRepository = academicYearRepository;
        this.learnerRepository = learnerRepository;
        this.learnerBasicDetailsRepository = learnerBasicDetailsRepository;
        this.learnerPastSchoolPerformanceRepository = learnerPastSchoolPerformanceRepository;
        this.learnerEntranceScoreRepository =learnerEntranceScoreRepository;
        this.learnerRubricsEvaluationRepository = learnerRubricsEvaluationRepository;

        
    }

 // 🔹 Principal Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {

        // Logged in user
        User user = userRepo.findByEmail(authentication.getName());

        // Find principal linked to this user
        com.iso.Model.Principal principal = principalRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        model.addAttribute("school", user.getSchool());
        model.addAttribute("educators",
                userRepo.findAllBySchoolAndRole(user.getSchool(), Role.EDUCATOR));
        model.addAttribute("learners",
                userRepo.findAllBySchoolAndRole(user.getSchool(), Role.LEARNER));
        model.addAttribute("totalmembers",
                userRepo.findAllBySchool(user.getSchool()));

        // 🔹 Needed for approve form navigation
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("principal", principal);

        return "principal/dashboard";
    }
    
    @GetMapping("/approve-form/{principalId}")
    public String loadApproveForm(@PathVariable Long principalId, Model model) {

        com.iso.Model.Principal principal = principalRepository.findById(principalId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        // If already approved
        if (principal.isPrincipalApproved()) {
            return "redirect:/principal/dashboard";
        }

        model.addAttribute("principal", principal);

        return "principal/approve-form";
    }
    
    @PostMapping("/approve/{principalId}")
    public String approveForm(@PathVariable Long principalId) {

        com.iso.Model.Principal principal = principalRepository.findById(principalId)
                .orElseThrow(() -> new RuntimeException("Principal not found"));

        principal.setPrincipalApproved(true);
        principal.setFormCompleted(true);

        principalRepository.save(principal);

        return "redirect:/principal/dashboard";
    }
    
 // 🔹 Show Academic Years
    @GetMapping("/show/academic-years")
    public String viewAcademicYears(Model model, Principal principal) {

        User principalUser = userRepo.findByEmail(principal.getName());
        List<AcademicYear> years = academicYearRepository.findBySchoolId(principalUser.getSchool().getId());

        model.addAttribute("years", years);
        model.addAttribute("activePage", "academicyear");
        return "principal/academic-years";
    }

    // 🔹 Show Create Form
    @GetMapping("/academic-years/create")
    public String showCreateAcademicYearForm(Model model) {
        model.addAttribute("academicYear", new AcademicYear());
        model.addAttribute("activePage", "createacademicyear");
        return "principal/create-academic-year";
    }

    // 🔹 Create Academic Year
    @PostMapping("/academic-years/create")
    public String createAcademicYear(@ModelAttribute AcademicYear academicYear,
                                     Principal principal,
                                     Model model) {

        User principalUser = userRepo.findByEmail(principal.getName());
        Long schoolId = principalUser.getSchool().getId();

        // Check if an academic year with the same name already exists in this school
        boolean exists = academicYearRepository.existsByNameAndSchoolId(
                academicYear.getName(), schoolId);

        if (exists) {
            // Return back to the form with an error message
            model.addAttribute("academicYear", academicYear);
            model.addAttribute("activePage", "createacademicyear");
            model.addAttribute("errorMessage", "Academic year with this name already exists.");
            return "principal/create-academic-year";
        }

        academicYear.setSchool(principalUser.getSchool());
        academicYear.setActive(false);

        academicYearRepository.save(academicYear);
        return "redirect:/principal/show/academic-years";
    }

    // 🔹 Activate Academic Year
    @GetMapping("/academic-years/activate/{id}")
    public String activateAcademicYear(@PathVariable Long id, Principal principal) {

        User principalUser = userRepo.findByEmail(principal.getName());

        // Deactivate currently active year
        academicYearRepository.findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .ifPresent(year -> {
                    year.setActive(false);
                    academicYearRepository.save(year);
                });

        // Activate selected year
        AcademicYear toActivate = academicYearRepository.findById(id)
                .orElseThrow();
        toActivate.setActive(true);
        academicYearRepository.save(toActivate);

        return "redirect:/principal/show/academic-years";
    }

    // 🔹 Delete Academic Year
    @GetMapping("/academic-years/delete/{id}")
    public String deleteAcademicYear(@PathVariable Long id,
    		Model model,
    		Principal principal) {

    	User principalUser = userRepo.findByEmail(principal.getName());
        try {
            AcademicYear academicYear = academicYearRepository.findById(id).orElseThrow();
            if(!academicYear.getSchool().getId().equals(principalUser.getSchool().getId())){
                throw new RuntimeException("Access denied");
            }
            academicYearRepository.delete(academicYear);
            model.addAttribute("success", "Section deleted successfully.");
        } catch (Exception e) {
            model.addAttribute("error", "Delete can't be performed due to risk of loss of data.");
        }

        model.addAttribute("sections", sectionRepository.findByClassRoomSchoolId(principalUser.getSchool().getId()));

        return "redirect:/principal/show/academic-years";
    }
    
    @GetMapping("/classrooms/create")
    public String showCreateClassroomForm(Model model, Principal principal) {
        User principalUser = userRepo.findByEmail(principal.getName());
        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        model.addAttribute("academicYears", activeYear);
        model.addAttribute("classRoom", new ClassRoom());
        model.addAttribute("activePage", "createclassroom");
        return "principal/create-classroom";
    }

    @PostMapping("/classrooms/create")
    public String createClassroom(@ModelAttribute ClassRoom classRoom,
                                  Principal principal,
                                  Model model){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));
        boolean exists = classRoomRepository.existsByNameAndSchoolIdAndAcademicYearId(
                classRoom.getName(), principalUser.getSchool().getId(), activeYear.getId());

        if (exists) {
            // Return back to the form with an error message
            model.addAttribute("classRoom", classRoom);
            model.addAttribute("academicYears", activeYear);
            model.addAttribute("activePage", "createclassroom");
            model.addAttribute("errorMessage", "Classroom with this name already exists for the active academic year.");
            return "principal/create-classroom";
        }
        classRoom.setSchool(principalUser.getSchool());
        classRoom.setAcademicYear(activeYear);

        classRoomRepository.save(classRoom);

        return "redirect:/principal/classrooms";
    }
    
    @GetMapping("/classrooms")
    public String viewClassrooms(Model model, Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<ClassRoom> classrooms =
                classRoomRepository.findByAcademicYearId(activeYear.getId());

        model.addAttribute("classrooms", classrooms);
        model.addAttribute("activePage", "classroom");
        return "principal/classrooms";
    }
    
    @GetMapping("/classrooms/search")
    public String searchClassrooms(@RequestParam String keyword,
                                   Model model,
                                   Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<ClassRoom> classrooms =
                classRoomRepository
                .findByNameContainingIgnoreCaseAndAcademicYearId(
                        keyword, activeYear.getId());

        model.addAttribute("classrooms", classrooms);
        model.addAttribute("activePage", "classroom");
        return "principal/classrooms";
    }
    
    @GetMapping("/classrooms/edit/{id}")
    public String editClassroomForm(@PathVariable Long id,
                                    Model model,
                                    Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        ClassRoom classroom = classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        if(!classroom.getAcademicYear().getId().equals(activeYear.getId())){
            throw new RuntimeException("You cannot edit classrooms from inactive academic years");
        }
       

        model.addAttribute("classRoom", classroom);
        model.addAttribute("activePage", "classroom");
        return "principal/edit-classroom";
    }
    
    @PostMapping("/classrooms/edit/{id}")
    public String updateClassroom(@PathVariable Long id,
                                  @ModelAttribute ClassRoom classRoom,
                                  Principal principal,
                                  Model model){

    	 User principalUser = userRepo.findByEmail(principal.getName());
    	 
        ClassRoom existing = classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        ClassRoom classroom = classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        if(!classroom.getAcademicYear().getId().equals(activeYear.getId())){
            throw new RuntimeException("You cannot edit classrooms from inactive academic years");
        }
        
        boolean exists = classRoomRepository.existsByNameAndSchoolIdAndAcademicYear(
                classRoom.getName(), principalUser.getSchool().getId(), activeYear);

        if (exists) {
            // Return back to the form with an error message
            model.addAttribute("classRoom", classRoom);
            model.addAttribute("academicYears", activeYear);
            model.addAttribute("activePage", "createclassroom");
            model.addAttribute("errorMessage", "Classroom with this name already exists for the active academic year.");
            return "redirect:/principal/classrooms/edit/" + id;
        }
        
        existing.setName(classRoom.getName());

        classRoomRepository.save(existing);
       

        return "redirect:/principal/classrooms";
    }
    
    @GetMapping("/classrooms/delete/{id}")
    public String deleteClassroom(@PathVariable Long id,
    		                      Principal principal,
                                  RedirectAttributes redirect,
                                  Model model){
    	User principalUser = userRepo.findByEmail(principal.getName());
        try {
            ClassRoom classroom = classRoomRepository.findById(id).orElseThrow();
            if(!classroom.getSchool().getId().equals(principalUser.getSchool().getId())){
                throw new RuntimeException("Access denied");
            }
            classRoomRepository.delete(classroom);
            model.addAttribute("success", "Section deleted successfully.");
        } catch (Exception e) {
            model.addAttribute("error", "Delete can't be performed due to risk of loss of data.");
        }

        model.addAttribute("classRooms", classRoomRepository.findBySchoolId(principalUser.getSchool().getId()));
        return "redirect:/principal/classrooms";
    }
 

   
    
    @GetMapping("/sections/create")
    public String showCreateSectionForm(Model model, Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<ClassRoom> classrooms =
                classRoomRepository.findByAcademicYearId(activeYear.getId());

        model.addAttribute("classrooms", classrooms);
        model.addAttribute("section", new Section());
        model.addAttribute("activePage", "createsection");
        return "principal/create-section";
    }
    
    @GetMapping("/classrooms-by-year/{yearId}")
    @ResponseBody
    public List<ClassRoom> getClassroomsByYear(@PathVariable Long yearId) {

        return classRoomRepository.findByAcademicYearId(yearId);
    }

    @PostMapping("/sections/create")
    public String createSection(@ModelAttribute Section section,
                                @RequestParam Long classRoomId,
                                Principal principal,
                                Model model){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        ClassRoom classRoom = classRoomRepository.findById(classRoomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        if(!classRoom.getAcademicYear().getId().equals(activeYear.getId())){
            throw new RuntimeException("Invalid classroom for active academic year");
        }
        boolean exists = sectionRepository.existsByNameAndClassRoomId(
                section.getName(),classRoom.getId());

        if (exists) {
            // Return back to the form with an error message
            model.addAttribute("section", section);
            model.addAttribute("academicYears", activeYear);
            model.addAttribute("activePage", "createsection");
            model.addAttribute("errorMessage", "Section with this name already exists for this classroom.");
            return "redirect:/principal/sections/create";
        }
        section.setClassRoom(classRoom);

        sectionRepository.save(section);

        return "redirect:/principal/sections";
    }
    
 // View Sections (with optional search)
    @GetMapping("/sections")
    public String viewSections(Model model, Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<Section> sections =
                sectionRepository.findByClassRoomAcademicYearId(activeYear.getId());

        model.addAttribute("sections", sections);
        model.addAttribute("activePage", "section");
        return "principal/sections";
    }
    
    @GetMapping("/sections/search")
    public String searchSections(@RequestParam String keyword,
                                 Model model,
                                 Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<Section> sections =
                sectionRepository
                        .findByNameContainingIgnoreCaseAndClassRoomAcademicYearId(
                                keyword, activeYear.getId());

        model.addAttribute("sections", sections);
        model.addAttribute("activePage", "section");
        return "principal/sections";
    }
    
    @GetMapping("/sections/edit/{id}")
    public String editSectionForm(@PathVariable Long id,
                                  Model model,
                                  Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        if(!section.getClassRoom().getSchool().getId().equals(principalUser.getSchool().getId())){
            throw new RuntimeException("Access denied");
        }
        
        // Get active academic year
        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        // Only classrooms of active academic year
        List<ClassRoom> classrooms =
                classRoomRepository.findByAcademicYearId(activeYear.getId());

        
        model.addAttribute("section", section);
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("activePage", "section");
        return "principal/edit-section";
    }

    
    @PostMapping("/sections/edit/{id}")
    public String editSection(@PathVariable Long id,
                              @RequestParam String sectionName,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {

        User principalUser = userRepo.findByEmail(principal.getName());
        Section section = sectionRepository.findById(id).orElseThrow();

        if (!section.getClassRoom().getSchool().getId().equals(principalUser.getSchool().getId())) {
            throw new RuntimeException("Access denied");
        }
     // ✅ Duplicate check (excluding current subject)
        boolean exists = sectionRepository
                .existsByNameIgnoreCaseAndClassRoomIdAndIdNot(
                        sectionName,
                        section.getClassRoom().getId(),
                        id
                );

        if (exists) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Section with this name already exists in this Class"
            );
            return "redirect:/principal/sections/edit/" + id;
        }
        section.setName(sectionName);
        sectionRepository.save(section);

        return "redirect:/principal/sections";
    }

    // Delete Section
    @GetMapping("/sections/delete/{id}")
    public String deleteSection(@PathVariable Long id, Principal principal, Model model) {
        User principalUser = userRepo.findByEmail(principal.getName());
        try {
            Section section = sectionRepository.findById(id).orElseThrow();
            if(!section.getClassRoom().getSchool().getId().equals(principalUser.getSchool().getId())){
                throw new RuntimeException("Access denied");
            }
            sectionRepository.delete(section);
            model.addAttribute("success", "Section deleted successfully.");
        } catch (Exception e) {
            model.addAttribute("error", "Delete can't be performed due to risk of loss of data.");
        }

        model.addAttribute("sections", sectionRepository.findByClassRoomSchoolId(principalUser.getSchool().getId()));
        return "principal/sections";
    }
    
    @GetMapping("/subjects/create")
    public String showCreateSubjectForm(Model model, Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<ClassRoom> classrooms =
                classRoomRepository.findByAcademicYearId(activeYear.getId());

        model.addAttribute("classrooms", classrooms);
        model.addAttribute("subject", new Subject());
        model.addAttribute("activePage", "createsubject");
        return "principal/create-subject";
    }
    
    @GetMapping("/sections-by-class/{classId}")
    @ResponseBody
    public List<SectionDTO> getSectionsByClass(@PathVariable Long classId){

        List<Section> sections = sectionRepository.findByClassRoomId(classId);

        return sections.stream()
                .map(s -> new SectionDTO(s.getId(), s.getName()))
                .toList();
    }
    
    @PostMapping("/subjects/create")
    public String createSubject(@RequestParam String name,
    		@RequestParam(value = "sectionIds", required = false) List<Long> sectionIds,
                                Principal principal,
                                Model model,
                                RedirectAttributes redirectAttributes ){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));
     
        
        // ✅ Handle empty sections (prevent 400 error)
        if(sectionIds == null || sectionIds.isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one section.");
            return "redirect:/principal/subjects/create";
        }
        
        for(Long sectionId : sectionIds){

            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new RuntimeException("Section not found"));

            if(!section.getClassRoom().getAcademicYear().getId()
                    .equals(activeYear.getId())){

                throw new RuntimeException("Invalid section for active academic year");
            }
            boolean exists = subjectRepository
                    .existsByNameIgnoreCaseAndSectionId(name, sectionId);
            if(exists){
                redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Subject '" + name + "' already exists in section " + section.getName()
                );
                return "redirect:/principal/subjects/create";
            }
            Subject subject = new Subject();
            subject.setName(name);
            subject.setSection(section);

            subjectRepository.save(subject);
        }

        return "redirect:/principal/subjects";
    }

 // View all subjects of active academic year(with optional search)
    @GetMapping("/subjects")
    public String viewSubjects(Model model, Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<Subject> subjects =
                subjectRepository.findBySectionClassRoomAcademicYearId(activeYear.getId());

        model.addAttribute("subjects", subjects);
        model.addAttribute("activePage", "subject");
        return "principal/subjects";
    }
    
    @GetMapping("/subjects/search")
    public String searchSubjects(@RequestParam String keyword,
                                 Model model,
                                 Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<Subject> subjects =
                subjectRepository
                .findByNameContainingIgnoreCaseAndSectionClassRoomAcademicYearId(
                        keyword, activeYear.getId());

        model.addAttribute("subjects", subjects);
        model.addAttribute("activePage", "subject");
        return "principal/subjects";
    }

 // Load edit page
 @GetMapping("/subjects/edit/{id}")
 public String editSubjectPage(@PathVariable Long id, Principal principal, Model model) {
     User principalUser = userRepo.findByEmail(principal.getName());
     Subject subject = subjectRepository.findById(id)
             .orElseThrow(() -> new RuntimeException("Subject not found"));

     if (!subject.getSection().getClassRoom().getSchool().getId().equals(principalUser.getSchool().getId())) {
         throw new RuntimeException("Access denied");
     }
     model.addAttribute("activePage", "subject");
     model.addAttribute("subject", subject);
     return "principal/edit-subject";
 }

 // Save edited subject
 @PostMapping("/subjects/edit/{id}")
 public String editSubject(@PathVariable Long id,
                           @RequestParam String subjectName,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {

     User principalUser = userRepo.findByEmail(principal.getName());
     Subject subject = subjectRepository.findById(id)
    		 .orElseThrow(() -> new RuntimeException("Subject not found"));

     if (!subject.getSection().getClassRoom().getSchool().getId().equals(principalUser.getSchool().getId())) {
         throw new RuntimeException("Access denied");
     }
     
  // ✅ Duplicate check (excluding current subject)
     boolean exists = subjectRepository
             .existsByNameIgnoreCaseAndSectionIdAndIdNot(
                     subjectName,
                     subject.getSection().getId(),
                     id
             );

     if (exists) {
         redirectAttributes.addFlashAttribute(
                 "errorMessage",
                 "Subject with this name already exists in this section"
         );
         return "redirect:/principal/subjects/edit/" + id;
     }

     subject.setName(subjectName);
     subjectRepository.save(subject);

     return "redirect:/principal/subjects";
 }

 // Delete subject
 @GetMapping("/subjects/delete/{id}")
 public String deleteSubject(@PathVariable Long id, Principal principal, Model model) {
     User principalUser = userRepo.findByEmail(principal.getName());
     try {
         Subject subject = subjectRepository.findById(id).orElseThrow();
         if(!subject.getSection().getClassRoom().getSchool().getId().equals(principalUser.getSchool().getId())){
             throw new RuntimeException("Access denied");
         }
         subjectRepository.delete(subject);
         model.addAttribute("success", "Subject deleted successfully.");
     } catch (Exception e) {
         model.addAttribute("error", "Delete can't be performed due to risk of loss of data.");
     }

     model.addAttribute("subjects", subjectRepository.findBySectionClassRoomSchoolId(principalUser.getSchool().getId()));
     model.addAttribute("activePage", "subject");
     return "principal/subjects";
 }
    
 @GetMapping("/units/create")
 public String showCreateUnitForm(Model model, Principal principal){

     User principalUser = userRepo.findByEmail(principal.getName());

     AcademicYear activeYear = academicYearRepository
             .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
             .orElseThrow(() -> new RuntimeException("No active academic year"));

     List<ClassRoom> classrooms =
             classRoomRepository.findByAcademicYearId(activeYear.getId());

     model.addAttribute("classrooms", classrooms);
     model.addAttribute("activePage", "createunit");
     return "principal/create-unit";
 }
    
    
    @GetMapping("/sections-by-classroom/{classId}")
    @ResponseBody
    public List<SectionDTO> getSectionsByClassRoom(@PathVariable Long classId){
        return sectionRepository.findByClassRoomId(classId)
                .stream()
                .map(s -> new SectionDTO(s.getId(), s.getName()))
                .toList();
    }
    
    @GetMapping("/subjectss-by-section/{sectionId}")
    @ResponseBody
    public List<SubjectsDTO> getSubjectssBySection(@PathVariable Long sectionId){

        return subjectRepository.findBySectionId(sectionId)
                .stream()
                .map(s -> new SubjectsDTO(
                        s.getId(),
                        s.getName(),
                        s.getSection().getName()
                ))
                .toList();
    }
   
 
    
    @PostMapping("/units/create")
    public String createUnit(@RequestParam String unitName,
                             @RequestParam List<Long> subjectIds,
                             Principal principal,
                             Model model){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        
        for(Long subjectId : subjectIds){

            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found"));

            if(!subject.getSection().getClassRoom().getAcademicYear().getId()
                    .equals(activeYear.getId())){

                throw new RuntimeException("Invalid subject for active academic year");
            }
         // duplicate check
            if(unitRepository.existsByNameAndSubjectId(unitName, subjectId)){
                model.addAttribute("error",
                        "Unit already exists for subject "
                                + subject.getName()
                                + " in section "
                                + subject.getSection().getName());
                return "principal/create-unit";
            }
            

            Unit unit = new Unit();
            unit.setName(unitName);
            unit.setSubject(subject);

            unitRepository.save(unit);
        }

        return "redirect:/principal/units";
    }
    
    // In order for viewing units
    @GetMapping("/units")
    public String viewUnits(Model model, Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<Unit> units =
                unitRepository.findBySubjectSectionClassRoomAcademicYearId(activeYear.getId());

        model.addAttribute("units", units);
        model.addAttribute("activePage", "unit");
        return "principal/view-units";
    }
    
    
    
    @GetMapping("/units/search")
    public String searchUnits(@RequestParam String keyword,
                              Model model,
                              Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<Unit> units =
                unitRepository
                .findByNameContainingIgnoreCaseAndSubjectSectionClassRoomAcademicYearId(
                        keyword, activeYear.getId());

        model.addAttribute("units", units);
        model.addAttribute("activePage", "unit");
        return "principal/view-units";
    }
    
    
    
    //In order for accessing edit units page
    @GetMapping("/units/edit/{id}")
    public String editUnitForm(@PathVariable Long id,
                               Model model,
                               Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        if(!unit.getSubject().getSection().getClassRoom().getAcademicYear().getId()
                .equals(activeYear.getId())){

            throw new RuntimeException("You cannot edit units from inactive academic years");
        }

        model.addAttribute("unit", unit);
        model.addAttribute("activePage", "unit");
        return "principal/edit-unit";
    }
    
    // In order for updating edited units
    @PostMapping("/units/update/{id}")
    public String updateUnit(@PathVariable Long id,
                             @RequestParam String unitName,
                             Principal principal,
                             RedirectAttributes redirectAttributes){

        User principalUser = userRepo.findByEmail(principal.getName());

        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        if(!unit.getSubject().getSection().getClassRoom().getSchool().getId()
                .equals(principalUser.getSchool().getId())){
            throw new RuntimeException("Unauthorized access");
        }
        // ✅ Duplicate check (excluding current subject)
        boolean exists = unitRepository
                .existsByNameIgnoreCaseAndSubjectIdAndIdNot(
                        unitName,
                        unit.getSubject().getId(),
                        id
                );

        if (exists) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unit with this name already exists in this subject"
            );
            return "redirect:/principal/units/edit/" + id;
        }

        unit.setName(unitName);

        unitRepository.save(unit);

        return "redirect:/principal/units";
    }
    
    //In order for deleting units
    @GetMapping("/units/delete/{id}")
    public String deleteUnit(@PathVariable Long id, Principal principal, Model model) {
        User principalUser = userRepo.findByEmail(principal.getName());
        try {
            Unit unit = unitRepository.findById(id).orElseThrow();
            if(!unit.getSubject().getSection().getClassRoom().getSchool().getId().equals(principalUser.getSchool().getId())){
                throw new RuntimeException("Access denied");
            }
            unitRepository.delete(unit);
            model.addAttribute("success", "Unit deleted successfully.");
        } catch (Exception e) {
            model.addAttribute("error", "Delete can't be performed due to risk of loss of data.");
        }

        model.addAttribute("units", unitRepository.findBySubjectSectionClassRoomSchoolId(principalUser.getSchool().getId()));
        model.addAttribute("activePage", "unit");
        return "principal/units";
    }
    
    
    @GetMapping("/chapters/create")
    public String showCreateChapterForm(Model model, Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<ClassRoom> classrooms =
                classRoomRepository.findByAcademicYearId(activeYear.getId());

        model.addAttribute("chapter", new Chapter());
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("activePage", "createchapter");
        return "principal/create-chapter";
    }
    
    @GetMapping("/units-by-subject/{subjectId}")
    @ResponseBody
    public List<UnitDTO> getUnits(@PathVariable Long subjectId){
        
        return unitRepository.findBySubjectId(subjectId)
                .stream()
                .map(s -> new UnitDTO(s.getId(), s.getName()))
                .toList();
    }
    
    @PostMapping("/chapters/create")
    public String createChapter(@RequestParam String chapterName,
                                @RequestParam Long unitId,
                                Principal principal,
                                RedirectAttributes redirectAttributes){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        if(!unit.getSubject().getSection().getClassRoom().getAcademicYear().getId()
                .equals(activeYear.getId())){

            throw new RuntimeException("Invalid unit for active academic year");
        }

        boolean exists = chapterRepository
                .existsByNameIgnoreCaseAndUnitId(chapterName, unitId);
        if(exists){
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Chapter '" + chapterName + "' already exists in unit " + unit.getName()
            );
            return "redirect:/principal/chapters/create";
        }
        Chapter chapter = new Chapter();
        chapter.setName(chapterName);
        chapter.setUnit(unit);

        chapterRepository.save(chapter);

        return "redirect:/principal/chapters";
    }
    
    @GetMapping("/chapters")
    public String viewChapters(Model model, Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<Chapter> chapters =
                chapterRepository.findByUnitSubjectSectionClassRoomAcademicYearId(activeYear.getId());

        model.addAttribute("chapters", chapters);
        model.addAttribute("activePage", "chapter");
        return "principal/view-chapters";
    }
    
    
    @GetMapping("/chapters/search")
    public String searchChapters(@RequestParam String keyword,
                                 Model model,
                                 Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<Chapter> chapters =
                chapterRepository
                .findByNameContainingIgnoreCaseAndUnitSubjectSectionClassRoomAcademicYearId(
                        keyword, activeYear.getId());

        model.addAttribute("chapters", chapters);
        model.addAttribute("activePage", "chapter");
        return "principal/view-chapters";
    }
    
    
    @GetMapping("/chapters/edit/{id}")
    public String editChapterPage(@PathVariable Long id,
                                  Principal principal,
                                  Model model){

        User principalUser = userRepo.findByEmail(principal.getName());

        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        if(!chapter.getUnit().getSubject().getSection().getClassRoom()
                .getSchool().getId()
                .equals(principalUser.getSchool().getId())){
            throw new RuntimeException("Unauthorized access");
        }

        model.addAttribute("chapter", chapter);
        model.addAttribute("activePage", "chapter");
        return "principal/edit-chapter";
    }
    
    
    @PostMapping("/chapters/update/{id}")
    public String updateChapter(@PathVariable Long id,
                                @RequestParam String chapterName,
                                Principal principal,
                                RedirectAttributes redirectAttributes){

        User principalUser = userRepo.findByEmail(principal.getName());

        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        if(!chapter.getUnit().getSubject().getSection().getClassRoom()
                .getSchool().getId()
                .equals(principalUser.getSchool().getId())){
            throw new RuntimeException("Unauthorized access");
        }
        
        // ✅ Duplicate check (excluding current subject)
        boolean exists = chapterRepository
                .existsByNameIgnoreCaseAndUnitIdAndIdNot(
                        chapterName,
                        chapter.getUnit().getId(),
                        id
                );

        if (exists) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Chapter with this name already exists in this Unit"
            );
            return "redirect:/principal/chapters/edit/" + id;
        }

        chapter.setName(chapterName);

        chapterRepository.save(chapter);

        return "redirect:/principal/chapters";
    }
    
    
    @GetMapping("/chapters/delete/{id}")
    public String deleteChapter(@PathVariable Long id, Principal principal, Model model) {
        User principalUser = userRepo.findByEmail(principal.getName());
        try {
            Chapter chapter = chapterRepository.findById(id).orElseThrow();
            if(!chapter.getUnit().getSubject().getSection().getClassRoom().getSchool().getId().equals(principalUser.getSchool().getId())){
                throw new RuntimeException("Access denied");
            }
            chapterRepository.delete(chapter);
            model.addAttribute("success", "Chapter deleted successfully.");
        } catch (Exception e) {
            model.addAttribute("error", "Delete can't be performed due to risk of loss of data.");
        }

        model.addAttribute("chapters", chapterRepository.findByUnitSubjectSectionClassRoomSchoolId(principalUser.getSchool().getId()));
        model.addAttribute("activePage", "chapter");
        return "principal/chapters";
    }
    
    
    
    

    // 🔹 Show Create Educator Form
    @GetMapping("/educators/create")
    public String showCreateEducatorForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("activePage", "createducator");
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
    
    @GetMapping("/subjects-by-section/{sectionId}")
    @ResponseBody
    public List<SubjectDTO> getSubjectsBySection(@PathVariable Long sectionId, Principal principal) {

        User principalUser = userRepo.findByEmail(principal.getName());

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        if(!section.getClassRoom().getSchool().getId().equals(principalUser.getSchool().getId())){
            throw new RuntimeException("Invalid section");
        }

        List<SubjectDTO> subjects = subjectRepository.findBySectionId(sectionId)
                .stream()
                .map(s -> new SubjectDTO(s.getId(), s.getName()))
                .toList();

        return subjects;
    }
    
    @GetMapping("/educator-assignment/create")
    public String showCreateEducatorAssignmentPage(Model model, Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<ClassRoom> classrooms =
                classRoomRepository.findByAcademicYearId(activeYear.getId());

        List<User> educators =
                userRepo.findAllBySchoolAndRole(principalUser.getSchool(), Role.EDUCATOR);

        model.addAttribute("classrooms", classrooms);
        model.addAttribute("educators", educators);
        model.addAttribute("activePage", "assigneducator");
        return "principal/create-educator-assignment";
    }
    
    @PostMapping("/educator-assignment/create")
    public String createEducatorAssignment(@RequestParam Long educatorId,
    		@RequestParam(value = "sectionIds", required = false) List<Long> sectionIds,
            @RequestParam(value = "subjectIds", required = false) List<Long> subjectIds,
                                           Principal principal,
                                           Model model,
                                           RedirectAttributes redirectAttributes){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        User educator = userRepo.findById(educatorId)
                .orElseThrow(() -> new RuntimeException("Educator not found"));
     // ✅ Handle missing sections or subjects
        if (sectionIds == null || sectionIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one section.");
            return "redirect:/principal/educator-assignment/create";
        }

        if (subjectIds == null || subjectIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one subject.");
            return "redirect:/principal/educator-assignment/create";
        }

        for(Long sectionId : sectionIds){

            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new RuntimeException("Section not found"));

            if(!section.getClassRoom().getAcademicYear().getId()
                    .equals(activeYear.getId())){
                throw new RuntimeException("Invalid section for active academic year");
            }

            for(Long subjectId : subjectIds){

                Subject subject = subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new RuntimeException("Subject not found"));

                if(!subject.getSection().getId().equals(sectionId)){
                    continue;
                }

                if(educatorAssignmentRepository
                        .existsByEducatorIdAndSubjectIdAndSectionId(
                                educatorId, subjectId, sectionId)){

                    continue;
                }

                EducatorAssignment assignment = new EducatorAssignment();

                assignment.setEducator(educator);
                assignment.setSection(section);
                assignment.setSubject(subject);

                educatorAssignmentRepository.save(assignment);
            }
        }

        return "redirect:/principal/educator-assignments";
    }
    
    @GetMapping("/educator-assignments")
    public String viewEducatorAssignments(Model model, Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<EducatorAssignment> assignments =
                educatorAssignmentRepository
                .findBySectionClassRoomAcademicYearId(activeYear.getId());

        model.addAttribute("assignments", assignments);
        model.addAttribute("activePage", "educatorassignment");
        return "principal/view-educator-assignments";
    }
    
    @GetMapping("/educator-assignments/search")
    public String searchAssignments(@RequestParam String keyword,
                                    Model model,
                                    Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<EducatorAssignment> assignments =
                educatorAssignmentRepository
                .findBySectionClassRoomAcademicYearId(activeYear.getId())
                .stream()
                .filter(a -> a.getEducator().getUsername()
                .toLowerCase().contains(keyword.toLowerCase()))
                .toList();

        model.addAttribute("assignments", assignments);
        model.addAttribute("activePage", "educatorassignment");
        return "principal/view-educator-assignments";
    }
    
    @GetMapping("/educator-assignment/delete/{id}")
    public String deleteEducatorAssignment(@PathVariable Long id, Principal principal,Model model){

        User principalUser = userRepo.findByEmail(principal.getName());

        EducatorAssignment assignment = educatorAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if(!assignment.getSection().getClassRoom().getSchool().getId()
                .equals(principalUser.getSchool().getId())){
            throw new RuntimeException("Unauthorized action");
        }

        educatorAssignmentRepository.delete(assignment);
        model.addAttribute("activePage", "educatorassignment");
        return "redirect:/principal/educator-assignments";
    }
    
    @PostMapping("/educator-assignment/toggle-restriction/{id}")
    public String toggleEducatorRestriction(@PathVariable Long id,
                                            RedirectAttributes redirect){

        EducatorAssignment assignment = educatorAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setRestricted(!assignment.isRestricted());
        educatorAssignmentRepository.save(assignment);

        redirect.addFlashAttribute("success",
                assignment.isRestricted() ? 
                "Educator access restricted for this subject" : 
                "Educator access unrestricted for this subject");

        return "redirect:/principal/educator-assignments";
    }
    

    // 🔹 Show Create Learner Form
    @GetMapping("/learners/create")
    public String showCreateLearnerForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("activePage", "createlearner");
        return "principal/create-learner";
    }

    // 🔹 Save Learner
    @PostMapping("/learners/create")
    public String createLearner(@ModelAttribute User user, Principal principal, Model model) {

        User principalUser = userRepo.findByEmail(principal.getName());
        School school = principalUser.getSchool();

        if (userRepo.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email already exists!");
            return "principal/create-learner";
        }

        user.setRole(Role.LEARNER);
        user.setSchool(school);
        user.setPassword(encoder.encode(user.getPassword()));

        userRepo.save(user);
        return "redirect:/principal/dashboard";
    }
    
    @GetMapping("/learners/enroll")
    public String showEnrollmentForm(Model model, Principal principal) {

        User principalUser = userRepo.findByEmail(principal.getName());

        // Active academic year
        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        // Only learners NOT enrolled in this academic year
        List<User> learners = userRepo.findNotEnrolledLearners(
                principalUser.getSchool(),
                Role.LEARNER,
                activeYear.getId()
        );

        // Classrooms of active year
        List<ClassRoom> classrooms =
                classRoomRepository.findByAcademicYearId(activeYear.getId());

        model.addAttribute("learners", learners);
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("activePage", "enrolllearner");
        return "principal/enroll-learner";
    }
    
    
    @PostMapping("/learners/enroll")
    public String enrollLearners( 
    		@RequestParam(value = "learnerIds", required = false) List<Long> learnerIds,
            @RequestParam(value = "sectionIds", required = false) List<Long> sectionIds,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));
 
     // ✅ Prevent 400 error (handle null/empty)
        if (learnerIds == null || learnerIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one learner.");
            return "redirect:/principal/learners/enroll";
        }

        if (sectionIds == null || sectionIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one section.");
            return "redirect:/principal/learners/enroll";
        }
        
        for(Long learnerId : learnerIds){
            User learner = userRepo.findById(learnerId)
                    .orElseThrow(() -> new RuntimeException("Learner not found"));

            for(Long sectionId : sectionIds){
                Section section = sectionRepository.findById(sectionId)
                        .orElseThrow(() -> new RuntimeException("Section not found"));

                // Prevent duplicate enrollment
                if(!learnerEnrollmentRepository.existsByLearnerIdAndSectionIdAndAcademicYearId(
                        learnerId, sectionId, activeYear.getId())) {

                    LearnerEnrollment enrollment = new LearnerEnrollment();
                    enrollment.setLearner(learner);
                    enrollment.setSection(section);
                    enrollment.setAcademicYear(activeYear);

                    learnerEnrollmentRepository.save(enrollment);
                }
            }
        }

        return "redirect:/principal/learners/enrollments";
    }
    
    
    
    
    
    @GetMapping("/learners/enrollments")
    public String viewEnrollments(Model model, Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear = academicYearRepository
                .findBySchoolIdAndActiveTrue(principalUser.getSchool().getId())
                .orElseThrow(() -> new RuntimeException("No active academic year"));

        List<ClassRoom> classrooms =
                classRoomRepository.findByAcademicYearId(activeYear.getId());
     // Get all learner enrollments for active year
        List<LearnerEnrollment> learnerEnrollments = learnerEnrollmentRepository
                .findByAcademicYearId(activeYear.getId());
        model.addAttribute("learnerEnrollments", learnerEnrollments);
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("activePage", "learnerenrollment");
        return "principal/view-learner-enrollments";
    }
    
    
    @GetMapping("/learners-by-section/{sectionId}")
    @ResponseBody
    public List<LearnerEnrollmentView> getLearnersBySection(
            @PathVariable Long sectionId){

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        Long academicYearId =
                section.getClassRoom().getAcademicYear().getId();

        return learnerEnrollmentRepository
                .findBySectionIdAndAcademicYearId(sectionId, academicYearId);
    }
    
    @PostMapping("/learners/enrollment/toggle/{id}")
    public String toggleEnrollment(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes){

        LearnerEnrollment enrollment =
                learnerEnrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setActive(!enrollment.isActive());

        learnerEnrollmentRepository.save(enrollment);

        if(enrollment.isActive()){
            redirectAttributes.addFlashAttribute("success","Learner activated");
        }else{
            redirectAttributes.addFlashAttribute("success","Learner deactivated");
        }

        return "redirect:/principal/learners/enrollments";
    }
    
    
    @PostMapping("/learners/enrollment/delete/{id}")
    public String deleteEnrollment(@PathVariable Long id){
        learnerEnrollmentRepository.deleteById(id);
        return "redirect:/principal/learners/enrollments";
    }
    
    
    @GetMapping("/learners/promotion")
    public String promotionPage(Model model, Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        AcademicYear activeYear =
                academicYearRepository.findByActiveTrue().orElseThrow();

        List<ClassRoom> classrooms =
                classRoomRepository.findBySchoolIdAndAcademicYearIdAndActiveTrue(
                        principalUser.getSchool().getId(),
                        activeYear.getId());

        List<AcademicYear> academicYears =
                academicYearRepository.findBySchoolId(principalUser.getSchool().getId());

        model.addAttribute("classrooms", classrooms);
        model.addAttribute("academicYears", academicYears);
        model.addAttribute("activeYear", activeYear);
        model.addAttribute("activePage", "learnerpromotion");
        return "principal/learner-promotion";
    }
    
    @GetMapping("/learners-by-section-for-promotion/{sectionId}")
    @ResponseBody
    public List<LearnerPromotionDTO> getLearnersForPromotion(@PathVariable Long sectionId){

        AcademicYear activeYear = academicYearRepository
                .findByActiveTrue()
                .orElseThrow();

        List<LearnerEnrollment> enrollments =
                learnerEnrollmentRepository
                .findAllBySectionIdAndAcademicYearIdAndActiveTrue(sectionId, activeYear.getId());

        return enrollments.stream().map(e ->
                new LearnerPromotionDTO(
                        e.getId(),
                        e.getLearner().getUsername(),
                        e.getLearner().getEmail()
                )
        ).toList();
    }
    
    @GetMapping("/academic-years")
    @ResponseBody
    public List<AcademicYear> getAcademicYears(Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        return academicYearRepository.findBySchoolId(principalUser.getSchool().getId());

    }
    
    @GetMapping("/classrooms-by-academic-year/{yearId}")
    @ResponseBody
    public List<ClassRoomDTO> getClassroomsByAcademicYear(@PathVariable Long yearId,
                                                          Principal principal){

        User principalUser = userRepo.findByEmail(principal.getName());

        List<ClassRoom> classrooms =
                classRoomRepository.findBySchoolIdAndAcademicYearIdAndActiveTrue(
                        principalUser.getSchool().getId(),
                        yearId
                );

        return classrooms.stream()
                .map(c -> new ClassRoomDTO(
                        c.getId(),
                        c.getName()   // change if your field is className
                ))
                .toList();
    }
    
    @PostMapping("/learners/promote")
    public String promoteLearners( 
    		@RequestParam(value = "enrollmentIds", required = false) List<Long> enrollmentIds,
            @RequestParam(value = "targetSectionId", required = false) Long targetSectionId,
            @RequestParam(value = "targetAcademicYearId", required = false) Long targetAcademicYearId,
            RedirectAttributes redirectAttributes){
    	// ✅ Prevent 400 error
        if(enrollmentIds == null || enrollmentIds.isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one learner.");
            return "redirect:/principal/learners/promotion";
        }

        if(targetSectionId == null){
            redirectAttributes.addFlashAttribute("errorMessage", "Please select target section.");
            return "redirect:/principal/learners/promotion";
        }

        if(targetAcademicYearId == null){
            redirectAttributes.addFlashAttribute("errorMessage", "Please select target academic year.");
            return "redirect:/principal/learners/promotion";
        }
        Section targetSection = sectionRepository.findById(targetSectionId)
        		.orElseThrow(() -> new RuntimeException("Section not found"));
        AcademicYear year = academicYearRepository.findById(targetAcademicYearId)
        		.orElseThrow(() -> new RuntimeException("Academic year not found"));;

        for(Long id : enrollmentIds){

            LearnerEnrollment enrollment =
                    learnerEnrollmentRepository.findById(id).orElseThrow();

            Long learnerId = enrollment.getLearner().getId();

            boolean exists =
                    learnerEnrollmentRepository.existsByLearnerIdAndSectionIdAndAcademicYearId(
                            learnerId,
                            targetSectionId,
                            targetAcademicYearId
                    );

            if(!exists){

                LearnerEnrollment newEnrollment = new LearnerEnrollment();

                newEnrollment.setLearner(enrollment.getLearner());
                newEnrollment.setSection(targetSection);
                newEnrollment.setAcademicYear(year);
                newEnrollment.setActive(true);

                learnerEnrollmentRepository.save(newEnrollment);
            }
        }

        return "redirect:/principal/learners/promotion";
    }
    
    @GetMapping("/learner-profile/{userId}")
    public String learnerProfile(@PathVariable Long userId, Model model) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Learner learner = learnerRepository.findByUser(user)
                .orElseGet(() -> {

                    Learner l = new Learner();
                    l.setUser(user); // ✅ VERY IMPORTANT
                    l.setSchool(user.getSchool()); // optional but recommended
                    l.setCurrentSection(1);
                    l.setIsCompleted(false);

                    return learnerRepository.save(l);
                });

        model.addAttribute("learner", learner);

        return "principal/learnerprofilelanding";
    }
    
    @GetMapping("/learners/section/1/{id}")
    public String loadSection1(@PathVariable Long id, Model model) {

        Learner learner = learnerRepository.findById(id).orElseThrow();

        // 🔒 Section validation
        if (!learner.getIsCompleted() && learner.getCurrentSection() != 1) {
            return "redirect:/principal/dashboard";
        }

        LearnerBasicDetails details = learnerBasicDetailsRepository.findByLearner(learner)
                .orElse(new LearnerBasicDetails());

        details.setLearner(learner);

        model.addAttribute("details", details);
        model.addAttribute("learner", learner);

        return "principal/learner-section1";
    }

    @PostMapping("/learners/section/1/save")
    public String saveSection1(@ModelAttribute("details") LearnerBasicDetails details) {

        Learner learner = learnerRepository.findById(details.getLearner().getId())
                .orElseThrow();

        details.setLearner(learner);

        learnerBasicDetailsRepository.save(details);

        // 👉 Move to next section
        if (learner.getCurrentSection() == 1) {
            learner.setCurrentSection(2);
            learnerRepository.save(learner);
        }

        return "redirect:/learners/section/2/" + learner.getId();
    }
   
    @GetMapping("/learners/section/edit/1/{id}")
    public String loadEditSection1(@PathVariable Long id, Model model) {

        Learner learner = learnerRepository.findById(id).orElseThrow();

        LearnerBasicDetails details = learnerBasicDetailsRepository.findByLearner(learner)
                .orElse(new LearnerBasicDetails());

        details.setLearner(learner);

        model.addAttribute("details", details);
        model.addAttribute("learner", learner);

        return "principal/learner-editsection1";
    }
    
    
    @PostMapping("/learners/section/edit/1/save")
    public String saveeditSection1(@ModelAttribute("details") LearnerBasicDetails details) {

        Learner learner = learnerRepository.findById(details.getLearner().getId())
                .orElseThrow();

        LearnerBasicDetails existingDetails = learnerBasicDetailsRepository.findByLearner(learner)
                .orElse(details); // If not exists, save new

        // Copy fields from form to existing entity
        existingDetails.setName(details.getName());
        existingDetails.setDateOfBirth(details.getDateOfBirth());
        existingDetails.setGender(details.getGender());
        existingDetails.setGradeAppliedFor(details.getGradeAppliedFor());
        existingDetails.setCurrentAddress(details.getCurrentAddress());
        existingDetails.setPermanentAddress(details.getPermanentAddress());
        existingDetails.setPreviousSchoolName(details.getPreviousSchoolName());
        existingDetails.setPreviousSchoolAddress(details.getPreviousSchoolAddress());
        existingDetails.setReasonForLeaving(details.getReasonForLeaving());

        existingDetails.setLearner(learner); // very important

        learnerBasicDetailsRepository.save(existingDetails);


        return "redirect:/learners/section/2/" + learner.getId();
    }
    
    @GetMapping("/learners/section/2/{id}")
    public String loadSection2(@PathVariable Long id, Model model) {

        Learner learner = learnerRepository.findById(id).orElseThrow();

        LearnerSection2DTO dto = new LearnerSection2DTO();

        // Part A
        LearnerPastSchoolPerformance performance = learnerPastSchoolPerformanceRepository.findByLearner(learner)
                .orElse(new LearnerPastSchoolPerformance());
        performance.setLearner(learner);

        // Part B
        LearnerEntranceScore entrance = learnerEntranceScoreRepository.findByLearner(learner)
                .orElse(new LearnerEntranceScore());
        entrance.setLearner(learner);

        // Part C
        LearnerRubricsEvaluation rubrics = learnerRubricsEvaluationRepository.findByLearner(learner)
                .orElse(new LearnerRubricsEvaluation());
        rubrics.setLearner(learner);

        dto.setPerformance(performance);
        dto.setEntrance(entrance);
        dto.setRubrics(rubrics);

        model.addAttribute("section2Dto", dto);

        return "principal/learner-section2";
    }
    
    @PostMapping("/learners/section/2/save")
    public String saveSection2(@ModelAttribute LearnerSection2DTO dto) {

        Learner learner = learnerRepository
                .findById(dto.getPerformance().getLearner().getId())
                .orElseThrow();

        // ================= Part A =================
        LearnerPastSchoolPerformance performance = dto.getPerformance();
        performance.setLearner(learner);

        if (performance.getSubjectMarks() != null) {
            performance.getSubjectMarks().forEach(s -> s.setPerformance(performance));
        }

        learnerPastSchoolPerformanceRepository.save(performance);

        // ================= Part B =================
        LearnerEntranceScore entrance = dto.getEntrance();
        entrance.setLearner(learner);

        if (entrance.getSubjects() != null) {
            entrance.getSubjects().forEach(s -> s.setLearnerEntranceScore(entrance));
        }

        learnerEntranceScoreRepository.save(entrance);

        // ================= Part C =================
        LearnerRubricsEvaluation rubrics = dto.getRubrics();
        rubrics.setLearner(learner);

        learnerRubricsEvaluationRepository.save(rubrics);

        // Move to next section
        if (learner.getCurrentSection() == 2) {
            learner.setCurrentSection(3);
            learnerRepository.save(learner);
        }

        return "redirect:/learners/section/3/" + learner.getId();
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
