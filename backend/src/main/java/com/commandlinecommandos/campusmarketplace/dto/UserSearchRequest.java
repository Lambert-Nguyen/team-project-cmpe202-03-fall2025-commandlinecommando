package com.commandlinecommandos.campusmarketplace.dto;

import com.commandlinecommandos.campusmarketplace.model.UserRole;
import com.commandlinecommandos.campusmarketplace.model.VerificationStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for searching and filtering users
 */
@Data
public class UserSearchRequest {
    
    private String searchTerm;  // Search in username, email, firstName, lastName
    private UserRole role;
    private VerificationStatus verificationStatus;
    private Boolean isActive;
    private String universityId;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private String sortBy = "createdAt";  // createdAt, lastName, email, lastLoginAt
    private String sortDirection = "DESC";  // ASC or DESC
    private Integer page = 0;
    private Integer size = 20;
}

