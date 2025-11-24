package com.commandlinecommandos.campusmarketplace.dto;

import com.commandlinecommandos.campusmarketplace.model.ProductCategory;
import com.commandlinecommandos.campusmarketplace.model.ProductCondition;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Search request DTO for product search
 * Includes filters, pagination, and sorting options
 */
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
    
    // Explicit getters and setters (Lombok fallback)
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public List<ProductCategory> getCategories() {
        return categories;
    }
    
    public void setCategories(List<ProductCategory> categories) {
        this.categories = categories;
    }
    
    public List<ProductCondition> getConditions() {
        return conditions;
    }
    
    public void setConditions(List<ProductCondition> conditions) {
        this.conditions = conditions;
    }
    
    public BigDecimal getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }
    
    public BigDecimal getMaxPrice() {
        return maxPrice;
    }
    
    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public LocalDateTime getDateFrom() {
        return dateFrom;
    }
    
    public void setDateFrom(LocalDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
}

