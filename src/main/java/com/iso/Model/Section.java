package com.iso.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;





@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;   // A, B, C
    
    private String code;   // G5-A , G6-B etc

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id")
    private ClassRoom classRoom;
    
    
    @OneToMany(mappedBy = "section")
    private List<LearnerEnrollment> learnersEnrollment;
    
    @OneToMany(mappedBy = "section")
    private List<EducatorAssignment> educatorAssignments;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    private List<Subject> subjects;
  
    
    @Column(nullable = false)
    private boolean deleted = false;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by_id")
    private User deletedBy;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ClassRoom getClassRoom() {
		return classRoom;
	}

	public void setClassRoom(ClassRoom classRoom) {
		this.classRoom = classRoom;
	}

	public List<LearnerEnrollment> getLearnersEnrollment() {
		return learnersEnrollment;
	}

	public void setLearnersEnrollment(List<LearnerEnrollment> learnersEnrollment) {
		this.learnersEnrollment = learnersEnrollment;
	}

	public List<EducatorAssignment> getEducatorAssignments() {
		return educatorAssignments;
	}

	public void setEducatorAssignments(List<EducatorAssignment> educatorAssignments) {
		this.educatorAssignments = educatorAssignments;
	}

	public List<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	public User getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(User deletedBy) {
		this.deletedBy = deletedBy;
	}
	
	
    

   
}