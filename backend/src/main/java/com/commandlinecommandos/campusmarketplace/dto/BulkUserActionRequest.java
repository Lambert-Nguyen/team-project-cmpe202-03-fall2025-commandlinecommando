package com.commandlinecommandos.campusmarketplace.dto;

import com.commandlinecommandos.campusmarketplace.model.UserRole;
import com.commandlinecommandos.campusmarketplace.model.VerificationStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO for bulk operations on users (max 100 users at once)
 */
@Data
public class BulkUserActionRequest {
    
    @NotEmpty(message = "User IDs list cannot be empty")
    @Size(max = 100, message = "Cannot perform bulk action on more than 100 users at once")
    private List<UUID> userIds;
    
    @NotNull(message = "Action is required")
    private BulkAction action;
    
    // Optional fields depending on action
    private UserRole newRole;  // For UPDATE_ROLE action
    private VerificationStatus newVerificationStatus;  // For UPDATE_VERIFICATION action
    private String reason;  // For SUSPEND or DELETE actions
    
    public enum BulkAction {
        ACTIVATE,
        DEACTIVATE,
        UPDATE_ROLE,
        UPDATE_VERIFICATION,
        SUSPEND,
        DELETE
    }
}

