package com.iso.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SchoolAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;        // CREATE_USER, EDIT_USER, etc.

    private String entityType;    // SCHOOL_USER, SCHOOL

    private Long entityId;        // ID of affected entity

    private String performedBy;   // Email of performer

    private String ipAddress;     // IP address

    @Column(length = 2000)
    private String details;       // Human readable message

    private LocalDateTime performedAt;

    // ===== Getters and Setters =====

    public Long getId() { 
    	return id; }

    public void setId(Long id) { 
    	this.id = id; }

    public String getAction() { 
    	return action; }

    public void setAction(String action) { 
    	this.action = action; }

    public String getEntityType() { 
    	return entityType; }

    public void setEntityType(String entityType) { 
    	this.entityType = entityType; }

    public Long getEntityId() { 
    	return entityId; }

    public void setEntityId(Long entityId) { 
    	this.entityId = entityId; }

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
