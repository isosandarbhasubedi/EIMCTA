package com.iso.Model.dto;


public class LearnerPromotionDTO {

    private Long enrollmentId;
    private String learnerName;
    private String learnerEmail;


    public LearnerPromotionDTO() {}

    public LearnerPromotionDTO(Long enrollmentId, String learnerName, String learnerEmail
               ) {
        this.enrollmentId = enrollmentId;
        this.learnerName = learnerName;
        this.learnerEmail = learnerEmail;

    }

    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }

    public String getLearnerName() { return learnerName; }
    public void setLearnerName(String learnerName) { this.learnerName = learnerName; }

    public String getLearnerEmail() { return learnerEmail; }
    public void setLearnerEmail(String learnerEmail) { this.learnerEmail = learnerEmail; }

}