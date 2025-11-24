package com.commandlinecommandos.campusmarketplace.repository;

import com.commandlinecommandos.campusmarketplace.model.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for LoginAttempt entity
 */
@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {
    
    /**
     * Find login attempts by username
     */
    List<LoginAttempt> findByUsername(String username);
    
    /**
     * Find failed login attempts by username within time window
     */
    @Query("SELECT la FROM LoginAttempt la WHERE la.username = :username AND la.success = false AND la.createdAt > :since")
    List<LoginAttempt> findFailedAttemptsByUsernameSince(String username, LocalDateTime since);
    
    /**
     * Find failed login attempts by IP within time window
     */
    @Query("SELECT la FROM LoginAttempt la WHERE la.ipAddress = :ipAddress AND la.success = false AND la.createdAt > :since")
    List<LoginAttempt> findFailedAttemptsByIpSince(String ipAddress, LocalDateTime since);
    
    /**
     * Count failed attempts by username within time window
     */
    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.username = :username AND la.success = false AND la.createdAt > :since")
    long countFailedAttemptsByUsernameSince(String username, LocalDateTime since);
    
    /**
     * Count failed attempts by IP within time window
     */
    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.ipAddress = :ipAddress AND la.success = false AND la.createdAt > :since")
    long countFailedAttemptsByIpSince(String ipAddress, LocalDateTime since);
    
    /**
     * Find recent successful login for username
     */
    @Query("SELECT la FROM LoginAttempt la WHERE la.username = :username AND la.success = true ORDER BY la.createdAt DESC")
    List<LoginAttempt> findRecentSuccessfulLogins(String username);
    
    /**
     * Delete old login attempts (cleanup)
     */
    @Modifying
    @Query("DELETE FROM LoginAttempt la WHERE la.createdAt < :before")
    void deleteOldAttempts(LocalDateTime before);
}

