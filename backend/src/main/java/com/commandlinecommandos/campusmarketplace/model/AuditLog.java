package com.commandlinecommandos.campusmarketplace.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Entity for comprehensive audit logging
 * Tracks all user actions, admin operations, and security events
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_user", columnList = "user_id"),
    @Index(name = "idx_audit_table", columnList = "table_name"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_created", columnList = "created_at"),
    @Index(name = "idx_audit_record", columnList = "record_id")
})
public class AuditLog {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "audit_id", updatable = false, nullable = false)
    private UUID auditId;
    
    // User who performed the action (can be null for system actions)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "username")
    private String username;  // Store username for reference even if user is deleted
    
    // Action details
    @Column(name = "table_name", nullable = false, length = 100)
    private String tableName;
    
    @Column(name = "record_id")
    private UUID recordId;
    
    @Column(name = "action", nullable = false, length = 50)
    private String action;  // CREATE, UPDATE, DELETE, LOGIN, LOGOUT, etc.
    
    // Change tracking
    @Type(JsonType.class)
    @Column(name = "old_values", columnDefinition = "TEXT")
    private Map<String, Object> oldValues;
    
    @Type(JsonType.class)
    @Column(name = "new_values", columnDefinition = "TEXT")
    private Map<String, Object> newValues;
    
    @Column(name = "description", length = 500)
    private String description;
    
    // Request context
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    // Severity for security events
    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private Severity severity = Severity.INFO;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
    
    public enum Severity {
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }
    
    // Constructors
    public AuditLog() {
    }
    
    // Constructor for quick audit log creation
    public AuditLog(User user, String tableName, String action, String description) {
        this.user = user;
        this.username = user != null ? user.getUsername() : "system";
        this.tableName = tableName;
        this.action = action;
        this.description = description;
    }

    // Getters and Setters
    public UUID getAuditId() {
        return auditId;
    }

    public void setAuditId(UUID auditId) {
        this.auditId = auditId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public UUID getRecordId() {
        return recordId;
    }

    public void setRecordId(UUID recordId) {
        this.recordId = recordId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getOldValues() {
        return oldValues;
    }

    public void setOldValues(Map<String, Object> oldValues) {
        this.oldValues = oldValues;
    }

    public Map<String, Object> getNewValues() {
        return newValues;
    }

    public void setNewValues(Map<String, Object> newValues) {
        this.newValues = newValues;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
