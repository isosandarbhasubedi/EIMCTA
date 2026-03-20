package com.iso.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iso.Model.School;
import com.iso.Model.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
	
	List<Subject> findBySectionClassRoomSchoolId(Long schoolId);
	
	List<Subject> findBySectionId(Long sectionId);
	
	//It is for creating unit where subject will be shown according to class
	List<Subject> findBySectionClassRoomId(Long classRoomId);
	
	// in order to check and select duplication of subject name
	boolean existsByNameIgnoreCaseAndSectionId(String name, Long sectionId);
	
	// In order to list subjects by section selection
	List<Subject> findBySectionIdIn(List<Long> sectionIds);
	
	//In order to find subject by academic year section
	List<Subject> findBySectionClassRoomAcademicYearId(Long academicYearId);

	//for search button in subject page with academic year
	List<Subject> findByNameContainingIgnoreCaseAndSectionClassRoomAcademicYearId(
	        String name, Long academicYearId);
	
	boolean existsByNameIgnoreCaseAndSectionIdAndIdNot(String name, Long sectionId, Long id);

}
