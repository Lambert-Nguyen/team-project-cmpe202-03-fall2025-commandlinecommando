package com.commandlinecommandos.campusmarketplace.repository;

import com.commandlinecommandos.campusmarketplace.model.AuditLog;
import com.commandlinecommandos.campusmarketplace.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for AuditLog entity
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    
    /**
     * Find logs by user with pagination
     */
    Page<AuditLog> findByUser(User user, Pageable pageable);
    
    /**
     * Find logs by username (for deleted users)
     */
    Page<AuditLog> findByUsername(String username, Pageable pageable);
    
    /**
     * Find logs by table name
     */
    Page<AuditLog> findByTableName(String tableName, Pageable pageable);
    
    /**
     * Find logs by action
     */
    Page<AuditLog> findByAction(String action, Pageable pageable);
    
    /**
     * Find logs by severity
     */
    Page<AuditLog> findBySeverity(AuditLog.Severity severity, Pageable pageable);
    
    /**
     * Find logs by record ID
     */
    List<AuditLog> findByRecordId(UUID recordId);
    
    /**
     * Find logs by date range
     */
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt BETWEEN :startDate AND :endDate ORDER BY al.createdAt DESC")
    Page<AuditLog> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find logs by user and date range
     */
    @Query("SELECT al FROM AuditLog al WHERE al.user = :user AND al.createdAt BETWEEN :startDate AND :endDate ORDER BY al.createdAt DESC")
    Page<AuditLog> findByUserAndDateRange(User user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Count logs by action
     */
    long countByAction(String action);
    
    /**
     * Count logs by severity
     */
    long countBySeverity(AuditLog.Severity severity);
    
    /**
     * Get recent critical logs
     */
    @Query("SELECT al FROM AuditLog al WHERE al.severity = 'CRITICAL' ORDER BY al.createdAt DESC")
    List<AuditLog> findRecentCriticalLogs(Pageable pageable);
}

