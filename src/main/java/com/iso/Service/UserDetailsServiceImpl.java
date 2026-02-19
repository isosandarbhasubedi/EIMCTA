package com.iso.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.iso.Model.User;
import com.iso.Repository.UserRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user == null) throw new UsernameNotFoundException("User not found");

        if (!user.isActive()) {
            throw new DisabledException("User is deactivated");
        }
        
        if (user.getDeleted()) {
            throw new DisabledException("User account has been deleted");
        }
     
        return new org.springframework.security.core.userdetails.User(
        	user.getEmail(), // important: use email as username
            user.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
    
}

