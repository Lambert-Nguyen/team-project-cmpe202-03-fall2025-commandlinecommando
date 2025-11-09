package com.commandlinecommandos.campusmarketplace.repository;

import com.commandlinecommandos.campusmarketplace.model.SearchHistory;
import com.commandlinecommandos.campusmarketplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for SearchHistory entity
 * Handles user search history operations
 */
@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, UUID> {
    
    /**
     * Find recent searches for a user
     * @param user The user
     * @param limit Maximum number of results
     * @return List of recent search queries
     */
    @Query("SELECT DISTINCT sh.searchQuery FROM SearchHistory sh " +
           "WHERE sh.user = :user " +
           "ORDER BY sh.createdAt DESC")
    List<String> findRecentSearchesByUser(@Param("user") User user, @Param("limit") int limit);
    
    /**
     * Find popular search queries (for autocomplete suggestions)
     * @param limit Maximum number of results
     * @return List of popular search queries
     */
    @Query(value = "SELECT search_query, COUNT(*) as count " +
           "FROM search_history " +
           "WHERE created_at > CURRENT_TIMESTAMP - INTERVAL '30 days' " +
           "GROUP BY search_query " +
           "ORDER BY count DESC, MAX(created_at) DESC " +
           "LIMIT :limit",
           nativeQuery = true)
    List<String> findPopularSearches(@Param("limit") int limit);
}

