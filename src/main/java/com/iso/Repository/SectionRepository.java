package com.iso.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.iso.Model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByClassRoomId(Long classRoomId);
    
    List<Section> findByClassRoomSchoolId(Long schoolId);

    boolean existsByNameAndClassRoomId(String name, Long classRoomId);
    boolean existsByName(String name);
    
    // For finding section by academic year classroom
    List<Section> findByClassRoomAcademicYearId(Long academicYearId);

    List<Section> findByNameContainingIgnoreCaseAndClassRoomAcademicYearId(
            String name, Long academicYearId);
    
    boolean existsByNameIgnoreCaseAndClassRoomIdAndIdNot(String name, Long classId, Long id);
}