package com.commandlinecommandos.campusmarketplace.repository;

import com.commandlinecommandos.campusmarketplace.model.User;
import com.commandlinecommandos.campusmarketplace.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for VerificationToken entity
 */
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    
    /**
     * Find token by token string
     */
    Optional<VerificationToken> findByToken(String token);
    
    /**
     * Find valid (unused and not expired) token by token string
     */
    @Query("SELECT vt FROM VerificationToken vt WHERE vt.token = :token AND vt.isUsed = false AND vt.expiresAt > :now")
    Optional<VerificationToken> findValidToken(String token, LocalDateTime now);
    
    /**
     * Find all tokens for a user
     */
    List<VerificationToken> findByUser(User user);
    
    /**
     * Find tokens by user and type
     */
    List<VerificationToken> findByUserAndTokenType(User user, VerificationToken.TokenType tokenType);
    
    /**
     * Find unused tokens for a user
     */
    @Query("SELECT vt FROM VerificationToken vt WHERE vt.user = :user AND vt.isUsed = false")
    List<VerificationToken> findUnusedTokensByUser(User user);
    
    /**
     * Delete expired tokens (cleanup task)
     */
    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
    
    /**
     * Delete all tokens for a user
     */
    void deleteByUser(User user);
}

