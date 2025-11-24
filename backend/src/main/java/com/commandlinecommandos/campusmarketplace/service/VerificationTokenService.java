package com.commandlinecommandos.campusmarketplace.service;

import com.commandlinecommandos.campusmarketplace.model.User;
import com.commandlinecommandos.campusmarketplace.model.VerificationToken;
import com.commandlinecommandos.campusmarketplace.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * Service for managing verification tokens (email verification, password reset)
 */
@Service
@Transactional
public class VerificationTokenService {
    
    @Autowired
    private VerificationTokenRepository tokenRepository;
    
    private static final int TOKEN_LENGTH = 32;
    private static final int EMAIL_VERIFICATION_EXPIRY_HOURS = 24;
    private static final int PASSWORD_RESET_EXPIRY_HOURS = 1;
    private static final int EMAIL_CHANGE_EXPIRY_HOURS = 24;
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Create email verification token
     */
    public VerificationToken createEmailVerificationToken(User user) {
        // Invalidate any existing email verification tokens for this user
        invalidateExistingTokens(user, VerificationToken.TokenType.EMAIL_VERIFICATION);
        
        VerificationToken token = new VerificationToken();
        token.setToken(generateSecureToken());
        token.setUser(user);
        token.setTokenType(VerificationToken.TokenType.EMAIL_VERIFICATION);
        token.setExpiresAt(LocalDateTime.now().plusHours(EMAIL_VERIFICATION_EXPIRY_HOURS));
        
        return tokenRepository.save(token);
    }
    
    /**
     * Create password reset token
     */
    public VerificationToken createPasswordResetToken(User user) {
        // Invalidate any existing password reset tokens for this user
        invalidateExistingTokens(user, VerificationToken.TokenType.PASSWORD_RESET);
        
        VerificationToken token = new VerificationToken();
        token.setToken(generateSecureToken());
        token.setUser(user);
        token.setTokenType(VerificationToken.TokenType.PASSWORD_RESET);
        token.setExpiresAt(LocalDateTime.now().plusHours(PASSWORD_RESET_EXPIRY_HOURS));
        
        return tokenRepository.save(token);
    }
    
    /**
     * Create email change verification token
     */
    public VerificationToken createEmailChangeToken(User user) {
        // Invalidate any existing email change tokens for this user
        invalidateExistingTokens(user, VerificationToken.TokenType.EMAIL_CHANGE);
        
        VerificationToken token = new VerificationToken();
        token.setToken(generateSecureToken());
        token.setUser(user);
        token.setTokenType(VerificationToken.TokenType.EMAIL_CHANGE);
        token.setExpiresAt(LocalDateTime.now().plusHours(EMAIL_CHANGE_EXPIRY_HOURS));
        
        return tokenRepository.save(token);
    }
    
    /**
     * Validate and retrieve token
     */
    public Optional<VerificationToken> validateToken(String tokenString) {
        Optional<VerificationToken> tokenOpt = tokenRepository.findValidToken(tokenString, LocalDateTime.now());
        
        if (tokenOpt.isEmpty()) {
            return Optional.empty();
        }
        
        VerificationToken token = tokenOpt.get();
        if (!token.isValid()) {
            return Optional.empty();
        }
        
        return Optional.of(token);
    }
    
    /**
     * Mark token as used
     */
    public void markTokenAsUsed(VerificationToken token) {
        token.markAsUsed();
        tokenRepository.save(token);
    }
    
    /**
     * Delete token
     */
    public void deleteToken(VerificationToken token) {
        tokenRepository.delete(token);
    }
    
    /**
     * Invalidate all unused tokens for a user by type
     */
    public void invalidateExistingTokens(User user, VerificationToken.TokenType tokenType) {
        var existingTokens = tokenRepository.findByUserAndTokenType(user, tokenType);
        for (VerificationToken token : existingTokens) {
            if (!token.isUsed()) {
                token.markAsUsed();
                tokenRepository.save(token);
            }
        }
    }
    
    /**
     * Clean up expired tokens (scheduled task)
     */
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
    
    /**
     * Generate cryptographically secure random token
     */
    private String generateSecureToken() {
        byte[] randomBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}

