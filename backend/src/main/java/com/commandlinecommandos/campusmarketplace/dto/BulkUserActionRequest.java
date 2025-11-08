package com.commandlinecommandos.campusmarketplace.dto;

import com.commandlinecommandos.campusmarketplace.model.UserRole;
import com.commandlinecommandos.campusmarketplace.model.VerificationStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * DTO for bulk operations on users (max 100 users at once)
 */
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

    // Constructors
    public BulkUserActionRequest() {
    }

    // Getters and Setters
    public List<UUID> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<UUID> userIds) {
        this.userIds = userIds;
    }

    public BulkAction getAction() {
        return action;
    }

    public void setAction(BulkAction action) {
        this.action = action;
    }

    public UserRole getNewRole() {
        return newRole;
    }

    public void setNewRole(UserRole newRole) {
        this.newRole = newRole;
    }

    public VerificationStatus getNewVerificationStatus() {
        return newVerificationStatus;
    }

    public void setNewVerificationStatus(VerificationStatus newVerificationStatus) {
        this.newVerificationStatus = newVerificationStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
