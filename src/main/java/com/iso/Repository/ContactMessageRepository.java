package com.iso.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.iso.Model.ContactMessage;
import com.iso.Model.ContactStatus;


public interface ContactMessageRepository 
        extends JpaRepository<ContactMessage, Long> {
	
List<ContactMessage> findAllByOrderByPerformedAtDesc();
    
    List<ContactMessage> findByStatusOrderByPerformedAtDesc(ContactStatus status);

}