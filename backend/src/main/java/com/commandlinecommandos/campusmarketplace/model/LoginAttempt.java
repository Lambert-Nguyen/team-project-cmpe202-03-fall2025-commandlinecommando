package com.commandlinecommandos.campusmarketplace.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for tracking login attempts and implementing account lockout
 */
@Entity
@Table(name = "login_attempts", indexes = {
    @Index(name = "idx_login_username", columnList = "username"),
    @Index(name = "idx_login_ip", columnList = "ip_address"),
    @Index(name = "idx_login_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
public class LoginAttempt {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "attempt_id", updatable = false, nullable = false)
    private UUID attemptId;
    
    @Column(name = "username", nullable = false, length = 50)
    private String username;
    
    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "success", nullable = false)
    private boolean success;
    
    @Column(name = "failure_reason", length = 200)
    private String failureReason;
    
    @Column(name = "device_info", length = 200)
    private String deviceInfo;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
    
    public LoginAttempt(String username, String ipAddress, boolean success) {
        this.username = username;
        this.ipAddress = ipAddress;
        this.success = success;
    }
    
    public LoginAttempt(String username, String ipAddress, boolean success, String failureReason) {
        this.username = username;
        this.ipAddress = ipAddress;
        this.success = success;
        this.failureReason = failureReason;
    }
}

