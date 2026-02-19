package com.iso.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iso.Model.Province;
import com.iso.Model.School;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {

    // Optional: find school by name if needed
    boolean existsByName(String name);
    
    void findAllByProvince(Province province);
    
    List<School> findByProvince(Province province);


}
