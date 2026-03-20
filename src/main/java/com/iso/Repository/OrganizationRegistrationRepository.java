package com.iso.Repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.iso.Model.OrganizationRegistration;
import com.iso.Model.RegistrationStatus;


public interface OrganizationRegistrationRepository 
        extends JpaRepository<OrganizationRegistration, Long> {
	
	

    List<OrganizationRegistration> findAllByOrderByPerformedAtDesc();
    
    List<OrganizationRegistration> findByStatusOrderByPerformedAtDesc(RegistrationStatus status);

}
