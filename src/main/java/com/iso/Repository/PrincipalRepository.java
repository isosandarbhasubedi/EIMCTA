package com.iso.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.iso.Model.Principal;
import com.iso.Model.User;

public interface PrincipalRepository extends JpaRepository<Principal, Long> {

    Principal findByUser(User user);
    Optional<Principal> findByUserId(Long userId);
}