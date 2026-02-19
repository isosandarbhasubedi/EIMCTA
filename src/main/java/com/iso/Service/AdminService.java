//package com.iso.Service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import com.iso.Model.User;
//import com.iso.Repository.UserRepository;
//
//import java.time.LocalDate;
//
//@Service
//public class AdminService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    public void createEmployee(User school) {
//
//        if (userRepository.existsByEmail(school.getEmail())) {
//            throw new RuntimeException("Employee already exists with this email");
//        }
//
//        school.setPassword(passwordEncoder.encode(school.getPassword()));
//        school.setRole("EMPLOYEE");
//
//        userRepository.save(school);
//    }
//}
