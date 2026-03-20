package com.iso.Model.dto;

public class SubjectDTO {
    private Long id;
    private String name;

    public SubjectDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}