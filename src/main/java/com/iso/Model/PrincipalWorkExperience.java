package com.iso.Model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class PrincipalWorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String grade;
    private String subjects;
    private String startDate;
    private String endDate;
    private String organizationName;
 // 🔥 CHANGE HERE
    @ElementCollection
    @CollectionTable(
        name = "principal_work_tools",
        joinColumns = @JoinColumn(name = "work_experience_id")
    )
    @Column(name = "tool")
    private List<String> tools = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "principal_id")
    private Principal principal;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getSubjects() {
		return subjects;
	}

	public void setSubjects(String subjects) {
		this.subjects = subjects;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	public List<String> getTools() {
		return tools;
	}

	public void setTools(List<String> tools) {
		this.tools = tools;
	}

	
    
    
    
}
