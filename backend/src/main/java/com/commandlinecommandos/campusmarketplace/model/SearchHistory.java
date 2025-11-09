package com.commandlinecommandos.campusmarketplace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Search History entity
 * Tracks user search queries for recent searches and analytics
 */
@Entity
@Table(name = "search_history", indexes = {
    @Index(name = "idx_search_history_user", columnList = "user_id,created_at"),
    @Index(name = "idx_search_history_query", columnList = "search_query"),
    @Index(name = "idx_search_history_created", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistory {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "search_query", nullable = false, length = 500)
    private String searchQuery;
    
    @Column(name = "results_count")
    private Integer resultsCount;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

