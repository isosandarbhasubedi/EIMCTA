package com.iso.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iso.Repository.ProvinceRepository;
import com.iso.Repository.SchoolRepository;
import com.iso.Repository.UserRepository;

@Controller
@RequestMapping("/edeo")
@PreAuthorize("hasRole('EDEO')")
public class EDEOController {
	
	 private final SchoolRepository schoolRepo;
	 private final UserRepository userRepo;
	    private final ProvinceRepository provinceRepo;
	    private final PasswordEncoder encoder;
	    
	    public EDEOController(SchoolRepository schoolRepo,
                UserRepository userRepo,
                PasswordEncoder encoder,
                ProvinceRepository provinceRepo) {
           this.schoolRepo = schoolRepo;
           this.userRepo = userRepo;
           this.encoder = encoder;
           this.provinceRepo = provinceRepo;
          }
	    
	    @GetMapping("/dashboard")
	    public String edeoDashboard() {
	        return "edeo/dashboard";
	    }

}
