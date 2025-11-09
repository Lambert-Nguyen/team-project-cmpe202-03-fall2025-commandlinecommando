package com.commandlinecommandos.campusmarketplace.controller;

import com.commandlinecommandos.campusmarketplace.dto.ProductSummary;
import com.commandlinecommandos.campusmarketplace.model.User;
import com.commandlinecommandos.campusmarketplace.repository.UserRepository;
import com.commandlinecommandos.campusmarketplace.security.JwtUtil;
import com.commandlinecommandos.campusmarketplace.service.DiscoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for product discovery features
 * Provides endpoints for trending, recommended, similar, and recently viewed items
 */
@RestController
@RequestMapping("/discovery")
@Tag(name = "Discovery", description = "Product discovery and recommendation endpoints")
@Slf4j
public class DiscoveryController {
    
    @Autowired
    private DiscoveryService discoveryService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get trending products
     * Returns most popular products based on views and favorites
     * 
     * @param limit Maximum number of products (default: 10)
     * @param token JWT authorization token
     * @return List of trending products
     */
    @GetMapping("/trending")
    @Operation(summary = "Get trending products",
               description = "Get trending products based on views and favorites")
    public ResponseEntity<List<ProductSummary>> getTrending(
            @Parameter(description = "Maximum number of products") 
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader("Authorization") String token) {
        
        try {
            User user = getCurrentUser(token);
            List<ProductSummary> trending = discoveryService.getTrendingItems(
                user.getUniversity().getUniversityId(), limit);
            
            log.debug("Trending items: user={}, count={}", user.getUsername(), trending.size());
            return ResponseEntity.ok(trending);
        } catch (AuthenticationException e) {
            log.warn("Unauthorized trending request: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("Trending error: {}", e.getMessage(), e);
            return ResponseEntity.ok(List.of());  // Return empty list on error
        }
    }
    
    /**
     * Get recommended products for current user
     * Based on browsing history and interests
     * 
     * @param limit Maximum number of products (default: 10)
     * @param token JWT authorization token
     * @return List of recommended products
     */
    @GetMapping("/recommended")
    @Operation(summary = "Get recommended products",
               description = "Get personalized product recommendations based on browsing history")
    public ResponseEntity<List<ProductSummary>> getRecommended(
            @Parameter(description = "Maximum number of products") 
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader("Authorization") String token) {
        
        try {
            User user = getCurrentUser(token);
            List<ProductSummary> recommended = discoveryService.getRecommendedItems(user, limit);
            
            log.debug("Recommended items: user={}, count={}", user.getUsername(), recommended.size());
            return ResponseEntity.ok(recommended);
        } catch (AuthenticationException e) {
            log.warn("Unauthorized recommended request: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("Recommended error: {}", e.getMessage(), e);
            return ResponseEntity.ok(List.of());  // Return empty list on error
        }
    }
    
    /**
     * Get similar products to a given product
     * Based on category and other attributes
     * 
     * @param productId Product UUID
     * @param limit Maximum number of products (default: 6)
     * @param token JWT authorization token (optional for public access)
     * @return List of similar products
     */
    @GetMapping("/similar/{productId}")
    @Operation(summary = "Get similar products",
               description = "Get products similar to a specific product")
    public ResponseEntity<List<ProductSummary>> getSimilar(
            @Parameter(description = "Product UUID") 
            @PathVariable UUID productId,
            @Parameter(description = "Maximum number of products") 
            @RequestParam(defaultValue = "6") int limit,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        try {
            // Similar items don't strictly require auth, but we validate if token is provided
            if (token != null && !token.isEmpty()) {
                getCurrentUser(token);  // Validate token if provided
            }
            
            List<ProductSummary> similar = discoveryService.getSimilarItems(productId, limit);
            
            log.debug("Similar items: productId={}, count={}", productId, similar.size());
            return ResponseEntity.ok(similar);
        } catch (AuthenticationException e) {
            log.warn("Invalid token for similar request: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("Similar items error: productId={}, error={}", productId, e.getMessage(), e);
            return ResponseEntity.ok(List.of());  // Return empty list on error
        }
    }
    
    /**
     * Get recently viewed products for current user
     * 
     * @param limit Maximum number of products (default: 10)
     * @param token JWT authorization token
     * @return List of recently viewed products
     */
    @GetMapping("/recently-viewed")
    @Operation(summary = "Get recently viewed products",
               description = "Get products recently viewed by the current user")
    public ResponseEntity<List<ProductSummary>> getRecentlyViewed(
            @Parameter(description = "Maximum number of products") 
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader("Authorization") String token) {
        
        try {
            User user = getCurrentUser(token);
            List<ProductSummary> recentlyViewed = discoveryService.getRecentlyViewedItems(user, limit);
            
            log.debug("Recently viewed: user={}, count={}", user.getUsername(), recentlyViewed.size());
            return ResponseEntity.ok(recentlyViewed);
        } catch (AuthenticationException e) {
            log.warn("Unauthorized recently-viewed request: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("Recently viewed error: {}", e.getMessage(), e);
            return ResponseEntity.ok(List.of());  // Return empty list on error
        }
    }
    
    /**
     * Helper method to extract user from JWT token
     * 
     * @param authHeader Authorization header with Bearer token
     * @return User object
     * @throws AuthenticationException if token is invalid or user not found
     */
    private User getCurrentUser(String authHeader) throws AuthenticationException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Invalid authorization header");
        }
        
        String token = authHeader.substring(7);  // Remove "Bearer " prefix
        String username = jwtUtil.extractUsername(token);
        
        if (username == null) {
            throw new AuthenticationException("Invalid token");
        }
        
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new AuthenticationException("User not found"));
    }
}

