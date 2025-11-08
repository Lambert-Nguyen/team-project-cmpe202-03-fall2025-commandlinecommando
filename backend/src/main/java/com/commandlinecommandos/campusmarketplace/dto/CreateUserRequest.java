package com.commandlinecommandos.campusmarketplace.dto;

import com.commandlinecommandos.campusmarketplace.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO for admin to create new user accounts
 */
public class CreateUserRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    private String phone;
    
    @NotNull(message = "Role is required")
    private UserRole role;
    
    @NotNull(message = "University ID is required")
    private UUID universityId;
    
    // A temporary password will be generated and sent via email
    private boolean sendWelcomeEmail = true;
    
    // Student-specific fields
    private String studentId;
    private Integer graduationYear;
    private String major;

    // Constructors
    public CreateUserRequest() {
    }

    // Getters and Setters
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UUID getUniversityId() {
        return universityId;
    }

    public void setUniversityId(UUID universityId) {
        this.universityId = universityId;
    }

    public boolean isSendWelcomeEmail() {
        return sendWelcomeEmail;
    }

    public void setSendWelcomeEmail(boolean sendWelcomeEmail) {
        this.sendWelcomeEmail = sendWelcomeEmail;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
