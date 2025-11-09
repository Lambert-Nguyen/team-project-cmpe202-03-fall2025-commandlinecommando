package com.commandlinecommandos.campusmarketplace.controller;

import com.commandlinecommandos.campusmarketplace.dto.SearchRequest;
import com.commandlinecommandos.campusmarketplace.dto.SearchResponse;
import com.commandlinecommandos.campusmarketplace.exception.UnauthorizedException;
import com.commandlinecommandos.campusmarketplace.model.User;
import com.commandlinecommandos.campusmarketplace.repository.UserRepository;
import com.commandlinecommandos.campusmarketplace.security.JwtUtil;
import com.commandlinecommandos.campusmarketplace.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for product search and autocomplete
 * Provides endpoints for searching, filtering, and discovery
 */
@RestController
@RequestMapping("/search")
@Tag(name = "Search", description = "Product search and discovery endpoints")
public class SearchController {
    
    private static final Logger log = LoggerFactory.getLogger(SearchController.class);
    
    @Autowired
    private SearchService searchService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Main search endpoint
     * Supports full-text search, filtering, sorting, and pagination
     * 
     * @param request Search request with query and filters
     * @param token JWT authorization token
     * @return Search response with results and metadata
     */
    @PostMapping
    @Operation(summary = "Search products", 
               description = "Search products with full-text search, filters, sorting, and pagination")
    public ResponseEntity<SearchResponse> search(
            @RequestBody SearchRequest request,
            @RequestHeader("Authorization") String token) {
        
        try {
            User user = getCurrentUser(token);
            SearchResponse response = searchService.search(request, user);
            
            log.info("Search request: user={}, query='{}', results={}",
                    user.getUsername(), request.getQuery(), response.getTotalResults());
            
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized search attempt: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("Search error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Autocomplete endpoint
     * Provides search suggestions as user types
     * 
     * @param query Search query (minimum 2 characters)
     * @param token JWT authorization token
     * @return List of title suggestions
     */
    @GetMapping("/autocomplete")
    @Operation(summary = "Get autocomplete suggestions",
               description = "Get search suggestions based on product titles")
    public ResponseEntity<List<String>> autocomplete(
            @Parameter(description = "Search query (min 2 characters)") 
            @RequestParam String q,
            @RequestHeader("Authorization") String token) {
        
        try {
            if (q == null || q.length() < 2) {
                return ResponseEntity.ok(List.of());
            }
            
            User user = getCurrentUser(token);
            List<String> suggestions = searchService.autocomplete(q, 
                user.getUniversity().getUniversityId());
            
            log.debug("Autocomplete: query='{}', suggestions={}", q, suggestions.size());
            return ResponseEntity.ok(suggestions);
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized autocomplete attempt: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("Autocomplete error: query='{}', error={}", q, e.getMessage(), e);
            return ResponseEntity.ok(List.of());  // Return empty list on error
        }
    }
    
    /**
     * Get search history for current user
     * Returns recent search queries
     * 
     * @param token JWT authorization token
     * @return List of recent search queries
     */
    @GetMapping("/history")
    @Operation(summary = "Get search history",
               description = "Get recent search queries for the current user")
    public ResponseEntity<List<String>> getSearchHistory(
            @RequestHeader("Authorization") String token) {
        
        try {
            User user = getCurrentUser(token);
            List<String> history = searchService.getRecentSearches(user.getUserId());
            
            log.debug("Search history: user={}, items={}", user.getUsername(), history.size());
            return ResponseEntity.ok(history);
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized search history attempt: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("Search history error: {}", e.getMessage(), e);
            return ResponseEntity.ok(List.of());  // Return empty list on error
        }
    }
    
    /**
     * Helper method to extract user from JWT token
     * 
     * @param authHeader Authorization header with Bearer token
     * @return User object
     * @throws UnauthorizedException if token is invalid or user not found
     */
    private User getCurrentUser(String authHeader) throws UnauthorizedException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid authorization header");
        }
        
        String token = authHeader.substring(7);  // Remove "Bearer " prefix
        String username = jwtUtil.extractUsername(token);
        
        if (username == null) {
            throw new UnauthorizedException("Invalid token");
        }
        
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
}

