package com.iso.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.iso.Model.Province;

public interface ProvinceRepository extends JpaRepository<Province, Long> {

    boolean existsByName(String name);
    
    

}
