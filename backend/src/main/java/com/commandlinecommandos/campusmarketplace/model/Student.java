package com.commandlinecommandos.campusmarketplace.model;

import jakarta.persistence.*;
// Removed Lombok dependencies - using manual getters/setters
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {
    
    @NotNull
    @Column(name = "student_id", unique = true)
    private String studentId;
    
    @Column(name = "major")
    private String major;
    
    @Column(name = "graduation_year")
    private Integer graduationYear;
    
    @Column(name = "campus_location")
    private String campusLocation;
    
    // Constructors
    public Student() {
        super();
        this.setRole(UserRole.STUDENT);
    }
    
    public Student(String username, String email, String password, String studentId, String major, Integer graduationYear, String campusLocation) {
        super(username, email, password, UserRole.STUDENT);
        this.studentId = studentId;
        this.major = major;
        this.graduationYear = graduationYear;
        this.campusLocation = campusLocation;
    }
    
    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getMajor() {
        return major;
    }
    
    public void setMajor(String major) {
        this.major = major;
    }
    
    public Integer getGraduationYear() {
        return graduationYear;
    }
    
    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }
    
    public String getCampusLocation() {
        return campusLocation;
    }
    
    public void setCampusLocation(String campusLocation) {
        this.campusLocation = campusLocation;
    }
    
    // Business method from UML diagram
    public boolean verifyStudentStatus() {
        // Verify if student ID is valid and student is currently enrolled
        return studentId != null && !studentId.isEmpty() && isActive();
    }
}
