package com.commandlinecommandos.campusmarketplace.service;

import com.commandlinecommandos.campusmarketplace.model.LoginAttempt;
import com.commandlinecommandos.campusmarketplace.repository.LoginAttemptRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * Service for tracking login attempts and implementing account lockout
 */
@Service
@Transactional
public class LoginAttemptService {
    
    @Autowired
    private LoginAttemptRepository loginAttemptRepository;
    
    @Autowired
    private AuditService auditService;
    
    // Configuration constants
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    private static final int ATTEMPT_WINDOW_MINUTES = 15;
    
    /**
     * Record successful login attempt
     */
    public void recordSuccessfulLogin(String username) {
        String ipAddress = getClientIpAddress();
        String userAgent = getUserAgent();
        
        LoginAttempt attempt = new LoginAttempt(username, ipAddress, true);
        attempt.setUserAgent(userAgent);
        loginAttemptRepository.save(attempt);
    }
    
    /**
     * Record failed login attempt
     */
    public void recordFailedLogin(String username, String failureReason) {
        String ipAddress = getClientIpAddress();
        String userAgent = getUserAgent();
        
        LoginAttempt attempt = new LoginAttempt(username, ipAddress, false, failureReason);
        attempt.setUserAgent(userAgent);
        loginAttemptRepository.save(attempt);
        
        // Log to audit service
        auditService.logFailedLogin(username, failureReason);
        
        // Check if account should be locked
        if (isAccountLocked(username)) {
            int attemptCount = getFailedAttemptCount(username);
            auditService.logAccountLockout(username, attemptCount);
        }
    }
    
    /**
     * Check if account is locked due to failed login attempts
     */
    public boolean isAccountLocked(String username) {
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(LOCKOUT_DURATION_MINUTES);
        long failedAttempts = loginAttemptRepository.countFailedAttemptsByUsernameSince(username, windowStart);
        return failedAttempts >= MAX_FAILED_ATTEMPTS;
    }
    
    /**
     * Check if IP is locked due to failed login attempts
     */
    public boolean isIpLocked(String ipAddress) {
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(LOCKOUT_DURATION_MINUTES);
        long failedAttempts = loginAttemptRepository.countFailedAttemptsByIpSince(ipAddress, windowStart);
        return failedAttempts >= MAX_FAILED_ATTEMPTS;
    }
    
    /**
     * Get failed attempt count within the attempt window
     */
    public int getFailedAttemptCount(String username) {
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(ATTEMPT_WINDOW_MINUTES);
        return (int) loginAttemptRepository.countFailedAttemptsByUsernameSince(username, windowStart);
    }
    
    /**
     * Get remaining lockout time in minutes
     */
    public int getRemainingLockoutTime(String username) {
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(LOCKOUT_DURATION_MINUTES);
        var attempts = loginAttemptRepository.findFailedAttemptsByUsernameSince(username, windowStart);
        
        if (attempts.isEmpty() || attempts.size() < MAX_FAILED_ATTEMPTS) {
            return 0;
        }
        
        // Get the oldest failed attempt within the window
        LocalDateTime oldestAttempt = attempts.stream()
            .map(LoginAttempt::getCreatedAt)
            .min(LocalDateTime::compareTo)
            .orElse(LocalDateTime.now());
        
        LocalDateTime lockoutEnd = oldestAttempt.plusMinutes(LOCKOUT_DURATION_MINUTES);
        long minutesRemaining = java.time.Duration.between(LocalDateTime.now(), lockoutEnd).toMinutes();
        
        return (int) Math.max(0, minutesRemaining);
    }
    
    /**
     * Reset failed attempts for username (after successful login)
     */
    public void resetFailedAttempts(String username) {
        // Attempts are naturally aged out, but we record the successful login
        recordSuccessfulLogin(username);
    }
    
    /**
     * Clean up old login attempts (scheduled task)
     */
    public void cleanupOldAttempts() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        loginAttemptRepository.deleteOldAttempts(cutoffDate);
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xfHeader = request.getHeader("X-Forwarded-For");
                if (xfHeader == null) {
                    return request.getRemoteAddr();
                }
                return xfHeader.split(",")[0].trim();
            }
        } catch (Exception e) {
            // Ignore
        }
        return "unknown";
    }
    
    /**
     * Get user agent from request
     */
    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            // Ignore
        }
        return "unknown";
    }
}

