package com.iso.Model;

import jakarta.persistence.*;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class LearnerBasicDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    
    private String gender;
    private String gradeAppliedFor;

    private String currentAddress;
    private String permanentAddress;

    private String previousSchoolName;
    private String previousSchoolAddress;

    @Column(length = 1000)
    private String reasonForLeaving;

    // 🔗 Link to Learner
    @OneToOne
    @JoinColumn(name = "learner_id", nullable = false)
    private Learner learner;

    // ===== Getters & Setters =====

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getGradeAppliedFor() { return gradeAppliedFor; }
    public void setGradeAppliedFor(String gradeAppliedFor) { this.gradeAppliedFor = gradeAppliedFor; }

    public String getCurrentAddress() { return currentAddress; }
    public void setCurrentAddress(String currentAddress) { this.currentAddress = currentAddress; }

    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }

    public String getPreviousSchoolName() { return previousSchoolName; }
    public void setPreviousSchoolName(String previousSchoolName) { this.previousSchoolName = previousSchoolName; }

    public String getPreviousSchoolAddress() { return previousSchoolAddress; }
    public void setPreviousSchoolAddress(String previousSchoolAddress) { this.previousSchoolAddress = previousSchoolAddress; }

    public String getReasonForLeaving() { return reasonForLeaving; }
    public void setReasonForLeaving(String reasonForLeaving) { this.reasonForLeaving = reasonForLeaving; }

    public Learner getLearner() { return learner; }
    public void setLearner(Learner learner) { this.learner = learner; }
}