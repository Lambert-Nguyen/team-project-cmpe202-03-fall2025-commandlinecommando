package com.commandlinecommandos.campusmarketplace.dto;

import com.commandlinecommandos.campusmarketplace.model.ProductCategory;
import com.commandlinecommandos.campusmarketplace.model.ProductCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Search request DTO for product search
 * Includes filters, pagination, and sorting options
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    
    /**
     * Search query keywords (searches title and description)
     */
    private String query;
    
    /**
     * Filter by categories (null = all categories)
     */
    private List<ProductCategory> categories;
    
    /**
     * Filter by product conditions (null = all conditions)
     */
    private List<ProductCondition> conditions;
    
    /**
     * Minimum price filter (inclusive)
     */
    private BigDecimal minPrice;
    
    /**
     * Maximum price filter (inclusive)
     */
    private BigDecimal maxPrice;
    
    /**
     * Location filter (searches pickup_location)
     */
    private String location;
    
    /**
     * Filter by date posted (null = all dates)
     * Can be set to last 24h, 7d, 30d, 90d
     */
    private LocalDateTime dateFrom;
    
    /**
     * Sort order
     * Values: relevance, price_asc, price_desc, date_desc, date_asc, popularity
     */
    private String sortBy = "relevance";
    
    /**
     * Page number (0-indexed)
     */
    private int page = 0;
    
    /**
     * Page size (number of results per page)
     */
    private int size = 20;
    
    /**
     * Generate cache key for Redis
     * Used to cache search results based on search parameters
     * 
     * @return Cache key string
     */
    public String cacheKey() {
        return String.format("%s_%s_%s_%s_%s_%s_%s_%d_%d", 
            query != null ? query : "all",
            categories != null ? categories.toString() : "all",
            conditions != null ? conditions.toString() : "all",
            minPrice != null ? minPrice : "0",
            maxPrice != null ? maxPrice : "max",
            location != null ? location : "all",
            sortBy != null ? sortBy : "relevance",
            page, 
            size);
    }
}

