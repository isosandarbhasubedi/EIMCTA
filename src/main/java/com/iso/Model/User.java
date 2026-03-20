package com.iso.Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    private String username;
    private String email;              // used for login
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role;

    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(nullable = false)
    private boolean deleted = false;
    
    
    @ManyToOne
    @JoinColumn(name = "deleted_by_id")
    private User deletedBy;

    private LocalDateTime deletedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school; // NULL only for EIMCTA USER
    
 // ===== Province Relation =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id")
    private Province province;   // For Province Users

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Principal principal;
    
    // ===== Constructors =====
    public User() {}

    
    // ===== Getters & Setters =====
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	
	public Role getRole() {
		return role;
	}


	public void setRole(Role role) {
		this.role = role;
	}


	

	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}


	public School getSchool() {
		return school;
	}
	
	


	public Boolean getDeleted() {
		return deleted;
	}


	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	

	public User getDeletedBy() {
		return deletedBy;
	}


	public void setDeletedBy(User deletedBy) {
		this.deletedBy = deletedBy;
	}


	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}


	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}


	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}


	public void setSchool(School school) {
		this.school = school;
	}


	public Province getProvince() {
		return province;
	}


	public void setProvince(Province province) {
		this.province = province;
	}


	public Principal getPrincipal() {
		return principal;
	}


	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

  
    

}
