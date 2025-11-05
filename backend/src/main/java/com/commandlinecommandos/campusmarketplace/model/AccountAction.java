package com.commandlinecommandos.campusmarketplace.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for tracking administrative actions on user accounts
 */
@Entity
@Table(name = "account_actions", indexes = {
    @Index(name = "idx_account_target_user", columnList = "target_user_id"),
    @Index(name = "idx_account_performed_by", columnList = "performed_by_id"),
    @Index(name = "idx_account_action_type", columnList = "action_type"),
    @Index(name = "idx_account_created", columnList = "created_at")
})
public class AccountAction {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "action_id", updatable = false, nullable = false)
    private UUID actionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id", nullable = false)
    private User performedBy;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;
    
    @Column(length = 500)
    private String reason;
    
    @Column(length = 1000)
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
    
    public enum ActionType {
        SUSPEND,
        UNSUSPEND,
        VERIFY,
        REJECT,
        ROLE_CHANGE,
        DELETE,
        REACTIVATE
    }
    
    // Constructors
    public AccountAction() {
    }
    
    public AccountAction(User targetUser, User performedBy, ActionType actionType, String reason) {
        this.targetUser = targetUser;
        this.performedBy = performedBy;
        this.actionType = actionType;
        this.reason = reason;
    }

    // Getters and Setters
    public UUID getActionId() {
        return actionId;
    }

    public void setActionId(UUID actionId) {
        this.actionId = actionId;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
