package com.commandlinecommandos.campusmarketplace.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Email Service for sending verification emails, password reset emails, etc.
 * 
 * NOTE: This is a basic implementation that logs emails.
 * For production, integrate with actual email service (SendGrid, AWS SES, etc.)
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private static final String FROM_EMAIL = "noreply@campusmarketplace.edu";
    private static final String SUPPORT_EMAIL = "support@campusmarketplace.edu";
    
    /**
     * Send email verification email
     */
    public void sendVerificationEmail(String to, String username, String token) {
        String verificationLink = buildVerificationLink(token);
        String subject = "Campus Marketplace - Email Verification";
        String body = buildVerificationEmailBody(username, verificationLink);
        
        sendEmail(to, subject, body);
        logger.info("Verification email sent to: {}", to);
    }
    
    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(String to, String username, String token) {
        String resetLink = buildPasswordResetLink(token);
        String subject = "Campus Marketplace - Password Reset Request";
        String body = buildPasswordResetEmailBody(username, resetLink);
        
        sendEmail(to, subject, body);
        logger.info("Password reset email sent to: {}", to);
        
        // Enhanced logging for development/testing
        logger.info("=====================================");
        logger.info("PASSWORD RESET TOKEN FOR TESTING");
        logger.info("Email: {}", to);
        logger.info("Token: {}", token);
        logger.info("Reset Link: {}", resetLink);
        logger.info("Token expires in 1 hour");
        logger.info("=====================================");
    }
    
    /**
     * Send welcome email with temporary password
     */
    public void sendWelcomeEmail(String to, String username, String temporaryPassword) {
        String subject = "Welcome to Campus Marketplace";
        String body = buildWelcomeEmailBody(username, temporaryPassword);
        
        sendEmail(to, subject, body);
        logger.info("Welcome email sent to: {}", to);
    }
    
    /**
     * Send account suspension notification
     */
    public void sendAccountSuspensionEmail(String to, String username, String reason) {
        String subject = "Campus Marketplace - Account Suspended";
        String body = buildAccountSuspensionEmailBody(username, reason);
        
        sendEmail(to, subject, body);
        logger.info("Account suspension email sent to: {}", to);
    }
    
    /**
     * Send account reactivation notification
     */
    public void sendAccountReactivationEmail(String to, String username) {
        String subject = "Campus Marketplace - Account Reactivated";
        String body = buildAccountReactivationEmailBody(username);
        
        sendEmail(to, subject, body);
        logger.info("Account reactivation email sent to: {}", to);
    }
    
    /**
     * Send login from new device notification
     */
    public void sendNewDeviceLoginEmail(String to, String username, String deviceInfo, String ipAddress) {
        String subject = "Campus Marketplace - New Device Login";
        String body = buildNewDeviceLoginEmailBody(username, deviceInfo, ipAddress);
        
        sendEmail(to, subject, body);
        logger.info("New device login email sent to: {}", to);
    }
    
    /**
     * Send password changed notification
     */
    public void sendPasswordChangedEmail(String to, String username) {
        String subject = "Campus Marketplace - Password Changed";
        String body = buildPasswordChangedEmailBody(username);
        
        sendEmail(to, subject, body);
        logger.info("Password changed email sent to: {}", to);
    }
    
    /**
     * Core email sending method
     * TODO: Replace with actual email service integration
     */
    private void sendEmail(String to, String subject, String body) {
        // For now, just log the email
        // In production, integrate with email service
        logger.info("=== EMAIL ===");
        logger.info("To: {}", to);
        logger.info("From: {}", FROM_EMAIL);
        logger.info("Subject: {}", subject);
        logger.info("Body: {}", body);
        logger.info("=============");
        
        // TODO: Integrate with actual email service
        // Example with Spring Mail:
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setFrom(FROM_EMAIL);
        // message.setTo(to);
        // message.setSubject(subject);
        // message.setText(body);
        // javaMailSender.send(message);
    }
    
    // Email template builders
    
    private String buildVerificationLink(String token) {
        // TODO: Replace with actual frontend URL from configuration
        return "http://localhost:3000/verify-email?token=" + token;
    }
    
    private String buildPasswordResetLink(String token) {
        // TODO: Replace with actual frontend URL from configuration
        return "http://localhost:3000/reset-password?token=" + token;
    }
    
    private String buildVerificationEmailBody(String username, String verificationLink) {
        return String.format(
            "Hello %s,\n\n" +
            "Thank you for registering with Campus Marketplace!\n\n" +
            "Please verify your email address by clicking the link below:\n%s\n\n" +
            "This link will expire in 24 hours.\n\n" +
            "If you did not create this account, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Campus Marketplace Team",
            username, verificationLink
        );
    }
    
    private String buildPasswordResetEmailBody(String username, String resetLink) {
        return String.format(
            "Hello %s,\n\n" +
            "We received a request to reset your password.\n\n" +
            "Click the link below to reset your password:\n%s\n\n" +
            "This link will expire in 1 hour.\n\n" +
            "If you did not request a password reset, please ignore this email and ensure your account is secure.\n\n" +
            "Best regards,\n" +
            "Campus Marketplace Team",
            username, resetLink
        );
    }
    
    private String buildWelcomeEmailBody(String username, String temporaryPassword) {
        return String.format(
            "Hello %s,\n\n" +
            "Welcome to Campus Marketplace!\n\n" +
            "Your account has been created by an administrator.\n\n" +
            "Username: %s\n" +
            "Temporary Password: %s\n\n" +
            "Please log in and change your password immediately.\n\n" +
            "Login at: http://localhost:3000/login\n\n" +
            "Best regards,\n" +
            "Campus Marketplace Team",
            username, username, temporaryPassword
        );
    }
    
    private String buildAccountSuspensionEmailBody(String username, String reason) {
        return String.format(
            "Hello %s,\n\n" +
            "Your Campus Marketplace account has been suspended.\n\n" +
            "Reason: %s\n\n" +
            "If you believe this is an error, please contact support at %s.\n\n" +
            "Best regards,\n" +
            "Campus Marketplace Team",
            username, reason, SUPPORT_EMAIL
        );
    }
    
    private String buildAccountReactivationEmailBody(String username) {
        return String.format(
            "Hello %s,\n\n" +
            "Great news! Your Campus Marketplace account has been reactivated.\n\n" +
            "You can now log in and continue using our services.\n\n" +
            "Best regards,\n" +
            "Campus Marketplace Team",
            username
        );
    }
    
    private String buildNewDeviceLoginEmailBody(String username, String deviceInfo, String ipAddress) {
        return String.format(
            "Hello %s,\n\n" +
            "We detected a login to your account from a new device:\n\n" +
            "Device: %s\n" +
            "IP Address: %s\n\n" +
            "If this was you, you can safely ignore this email.\n\n" +
            "If you did not log in from this device, please:\n" +
            "1. Change your password immediately\n" +
            "2. Contact support at %s\n\n" +
            "Best regards,\n" +
            "Campus Marketplace Team",
            username, deviceInfo, ipAddress, SUPPORT_EMAIL
        );
    }
    
    private String buildPasswordChangedEmailBody(String username) {
        return String.format(
            "Hello %s,\n\n" +
            "Your password has been successfully changed.\n\n" +
            "If you did not make this change, please contact support immediately at %s.\n\n" +
            "Best regards,\n" +
            "Campus Marketplace Team",
            username, SUPPORT_EMAIL
        );
    }
}

