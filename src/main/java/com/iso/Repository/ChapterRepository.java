package com.iso.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iso.Model.Chapter;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
	
	List<Chapter> findByUnitSubjectSectionClassRoomSchoolId(Long schoolId);

	//In order to find unit,section,class according to academinc year
	List<Chapter> findByUnitSubjectSectionClassRoomAcademicYearId(Long academicYearId);

	//for search button in chapter page with academic year
	List<Chapter> findByNameContainingIgnoreCaseAndUnitSubjectSectionClassRoomAcademicYearId(
	        String name, Long academicYearId);
	boolean existsByNameIgnoreCaseAndUnitIdAndIdNot(String name, Long unitId, Long id);
	// in order to check and select duplication of chapter name
	boolean existsByNameIgnoreCaseAndUnitId(String name, Long unitId);
}
