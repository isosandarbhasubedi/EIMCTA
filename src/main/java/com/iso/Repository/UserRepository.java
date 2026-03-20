package com.iso.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iso.Model.Province;
import com.iso.Model.Role;
import com.iso.Model.School;
import com.iso.Model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
    boolean existsByEmail(String email);
    
    User findByUsername(String username);
    List<User> findByRole(String role);
    
 // ✅ This method is to fetch all users by school and role
    List<User> findAllBySchoolAndRole(School school, Role role);
    
    List<User> findAllBySchool(School school);

    
    @Query("SELECT u FROM User u JOIN FETCH u.school WHERE u.email = :email")
    User findByEmailWithSchool(@Param("email") String email);

    List<User> findByProvince(Province province);

    List<User> findBySchool(School school);
    
    //To find user by province and role
    List<User> findByProvinceAndRoleIn(Province province, List<Role> roles);

    
    //to do related task of deactive, activate, delete, recover user
    List<User> findByRoleInAndDeletedFalse(List<Role> roles);

    List<User> findByRoleInAndDeletedTrue(List<Role> roles);


    long countByProvinceAndRoleAndActiveTrue(Province province, Role role);
    
    //In order for enrolling learners who are not enrolled
    @Query("""
    		SELECT u FROM User u
    		WHERE u.school = :school
    		AND u.role = :role
    		AND u.id NOT IN (
    		    SELECT le.learner.id
    		    FROM LearnerEnrollment le
    		    WHERE le.academicYear.id = :academicYearId
    		)
    		""")
    		List<User> findNotEnrolledLearners(School school, Role role, Long academicYearId);
    
    }

