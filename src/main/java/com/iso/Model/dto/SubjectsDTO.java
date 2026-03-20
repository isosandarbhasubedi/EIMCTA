package com.iso.Model.dto;

public class SubjectsDTO {

    private Long id;
    private String name;
    private String sectionName;

    public SubjectsDTO(Long id, String name, String sectionName) {
        this.id = id;
        this.name = name;
        this.sectionName = sectionName;
    }
    
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSectionName() { return sectionName; }
  

}
