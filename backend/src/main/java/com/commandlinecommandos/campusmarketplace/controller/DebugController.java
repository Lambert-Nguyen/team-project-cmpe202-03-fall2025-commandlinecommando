package com.commandlinecommandos.campusmarketplace.controller;

import com.commandlinecommandos.campusmarketplace.model.UserRole;
import com.commandlinecommandos.campusmarketplace.model.VerificationToken;
import com.commandlinecommandos.campusmarketplace.repository.VerificationTokenRepository;
import com.commandlinecommandos.campusmarketplace.security.RequireRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Debug controller for development and testing
 * WARNING: This controller should be disabled in production
 */
@RestController
@RequestMapping("/debug")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, maxAge = 3600)
public class DebugController {
    
    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);
    
    @Autowired
    private VerificationTokenRepository tokenRepository;
    
    /**
     * Get active reset tokens (development only)
     * WARNING: This endpoint should be disabled in production
     */
    @GetMapping("/reset-tokens")
    @RequireRole(UserRole.ADMIN)
    public ResponseEntity<?> getActiveResetTokens() {
        try {
            logger.info("Admin requesting active reset tokens for testing");
            
            // Get all valid PASSWORD_RESET tokens
            List<VerificationToken> tokens = tokenRepository.findAll().stream()
                .filter(t -> t.getTokenType() == VerificationToken.TokenType.PASSWORD_RESET)
                .filter(t -> !t.isUsed())
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .toList();
            
            List<Map<String, Object>> tokenInfo = tokens.stream()
                .map(t -> {
                    Map<String, Object> info = new java.util.HashMap<>();
                    info.put("email", t.getUser().getEmail());
                    info.put("username", t.getUser().getUsername());
                    info.put("token", t.getToken());
                    info.put("expiresAt", t.getExpiresAt().toString());
                    info.put("createdAt", t.getCreatedAt().toString());
                    return info;
                })
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "activeTokens", tokenInfo,
                "count", tokenInfo.size(),
                "message", "Active password reset tokens for testing purposes"
            ));
        } catch (Exception e) {
            logger.error("Error retrieving reset tokens", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to retrieve tokens", "message", e.getMessage()));
        }
    }
}

