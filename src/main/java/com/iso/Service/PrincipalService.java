package com.iso.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iso.Model.Principal;
import com.iso.Model.User;
import com.iso.Repository.PrincipalRepository;

@Service
public class PrincipalService {

    @Autowired
    private PrincipalRepository principalRepo;

    public Principal savePrincipal(Principal principal) {
        return principalRepo.save(principal);
    }

    public Principal getByUser(User user) {
        return principalRepo.findByUser(user);
    }
}