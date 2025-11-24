package com.commandlinecommandos.campusmarketplace.controller;

import com.commandlinecommandos.campusmarketplace.dto.ForgotPasswordRequest;
import com.commandlinecommandos.campusmarketplace.dto.ResetPasswordRequest;
import com.commandlinecommandos.campusmarketplace.service.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Controller for password reset functionality
 * Handles forgot password and reset password with token
 */
@RestController
@RequestMapping("/auth")
@Validated
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, maxAge = 3600)
public class PasswordResetController {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);
    
    @Autowired
    private UserManagementService userManagementService;
    
    /**
     * Initiate password reset (forgot password)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            userManagementService.initiatePasswordReset(request);
            // Always return success to prevent email enumeration
            return ResponseEntity.ok(Map.of(
                "message", "If an account exists with this email, a password reset link has been sent"
            ));
        } catch (Exception e) {
            logger.error("Error processing forgot password request", e);
            // Still return success to prevent email enumeration
            return ResponseEntity.ok(Map.of(
                "message", "If an account exists with this email, a password reset link has been sent"
            ));
        }
    }
    
    /**
     * Reset password with token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            userManagementService.resetPassword(request);
            return ResponseEntity.ok(Map.of(
                "message", "Password reset successfully. You can now log in with your new password."
            ));
        } catch (Exception e) {
            logger.error("Error resetting password", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Password reset failed", "message", e.getMessage()));
        }
    }
}

