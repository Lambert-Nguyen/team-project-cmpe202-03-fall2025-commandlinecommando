package com.commandlinecommandos.campusmarketplace.repository;

import com.commandlinecommandos.campusmarketplace.model.AccountAction;
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
 * Repository for AccountAction entity
 */
@Repository
public interface AccountActionRepository extends JpaRepository<AccountAction, UUID> {
    
    /**
     * Find actions by user
     */
    List<AccountAction> findByUser(User user);
    
    /**
     * Find actions by user with pagination
     */
    Page<AccountAction> findByUser(User user, Pageable pageable);
    
    /**
     * Find actions performed by admin
     */
    List<AccountAction> findByPerformedBy(User admin);
    
    /**
     * Find actions by type
     */
    List<AccountAction> findByActionType(AccountAction.ActionType actionType);
    
    /**
     * Find unrevertedactions that are scheduled for revert
     */
    @Query("SELECT aa FROM AccountAction aa WHERE aa.isReverted = false AND aa.scheduledRevertAt IS NOT NULL AND aa.scheduledRevertAt < :now")
    List<AccountAction> findActionsScheduledForRevert(LocalDateTime now);
    
    /**
     * Find recent actions for user
     */
    @Query("SELECT aa FROM AccountAction aa WHERE aa.user = :user ORDER BY aa.createdAt DESC")
    List<AccountAction> findRecentActionsByUser(User user, Pageable pageable);
    
    /**
     * Count actions by type
     */
    long countByActionType(AccountAction.ActionType actionType);
}

