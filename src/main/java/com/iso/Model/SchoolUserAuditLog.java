package com.iso.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SchoolUserAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;        // CREATE_USER, EDIT_USER, DELETE_USER, etc.

    private Long userId;          // ID of affected school user

    private String userEmail;     // Email of affected user

    private String userRole;      // Role of affected user

    private String performedBy;   // Who performed the action

    private String ipAddress;

    @Column(length = 2000)
    private String details;

    private LocalDateTime performedAt;

    // ===== Getters & Setters =====

    public Long getId() { 
    	return id; }

    public void setId(Long id) { 
    	this.id = id; }

    public String getAction() { 
    	return action; }

    public void setAction(String action) { 
    	this.action = action; }

    public Long getUserId() { 
    	return userId; }

    public void setUserId(Long userId) { 
    	this.userId = userId; }

    public String getUserEmail() { 
    	return userEmail; }

    public void setUserEmail(String userEmail) { 
    	this.userEmail = userEmail; }

    public String getUserRole() { 
    	return userRole; }

    public void setUserRole(String userRole) { 
    	this.userRole = userRole; }

    public String getPerformedBy() { 
    	return performedBy; }

    public void setPerformedBy(String performedBy) { 
    	this.performedBy = performedBy; }

    public String getIpAddress() { 
    	return ipAddress; }

    public void setIpAddress(String ipAddress) { 
    	this.ipAddress = ipAddress; }

    public String getDetails() { 
    	return details; }

    public void setDetails(String details) { 
    	this.details = details; }

    public LocalDateTime getPerformedAt() { 
    	return performedAt; }

    public void setPerformedAt(LocalDateTime performedAt) { 
    	this.performedAt = performedAt; }
}
