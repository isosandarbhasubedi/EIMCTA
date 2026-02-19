package com.iso.Config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.iso.Model.Role;
import com.iso.Model.User;
import com.iso.Repository.UserRepository;



@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) {
    	if (!userRepository.existsByEmail("superadmineimcta@gmail.com")) {
    	    User admin = new User();
    	    admin.setEmail("superadmineimcta@gmail.com");
    	    admin.setPassword(encoder.encode("Sandarbha@123"));
    	    admin.setRole(Role.SUPER_ADMIN);
    	    admin.setUsername("Superadmin");
    	    admin.setSchool(null);
    	    userRepository.save(admin);
    	}

    
}
}

