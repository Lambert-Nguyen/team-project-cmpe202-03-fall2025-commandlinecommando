package com.commandlinecommandos.campusmarketplace.service;

import com.commandlinecommandos.campusmarketplace.model.SearchHistory;
import com.commandlinecommandos.campusmarketplace.model.User;
import com.commandlinecommandos.campusmarketplace.repository.SearchHistoryRepository;
import com.commandlinecommandos.campusmarketplace.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing search history
 * Tracks user searches for analytics and recent searches feature
 */
@Service
@Slf4j
public class SearchHistoryService {
    
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Save search query asynchronously
     * Does not block the search operation
     * 
     * @param userId User performing the search
     * @param query Search query
     * @param resultsCount Number of results found
     */
    @Async
    @Transactional
    public void saveSearchAsync(UUID userId, String query, int resultsCount) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return;  // Don't save empty queries
            }
            
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                log.warn("User not found for search history: {}", userId);
                return;
            }
            
            SearchHistory searchHistory = SearchHistory.builder()
                .user(user)
                .searchQuery(query.trim())
                .resultsCount(resultsCount)
                .build();
                
            searchHistoryRepository.save(searchHistory);
            log.debug("Saved search history for user {}: query={}, results={}", 
                     userId, query, resultsCount);
        } catch (Exception e) {
            log.error("Error saving search history for user {}: {}", userId, e.getMessage(), e);
            // Don't throw exception - search history failure should not break search
        }
    }
    
    /**
     * Save search query asynchronously (without results count)
     * 
     * @param userId User performing the search
     * @param query Search query
     */
    @Async
    @Transactional
    public void saveSearchAsync(UUID userId, String query) {
        saveSearchAsync(userId, query, 0);
    }
    
    /**
     * Get recent searches for a user
     * 
     * @param userId User ID
     * @param limit Maximum number of results
     * @return List of recent search queries
     */
    public List<String> getRecentSearches(UUID userId, int limit) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return List.of();
        }
        
        return searchHistoryRepository.findRecentSearchesByUser(user, limit);
    }
    
    /**
     * Get popular search queries
     * Used for autocomplete suggestions
     * 
     * @param limit Maximum number of results
     * @return List of popular search queries
     */
    public List<String> getPopularSearches(int limit) {
        return searchHistoryRepository.findPopularSearches(limit);
    }
}

