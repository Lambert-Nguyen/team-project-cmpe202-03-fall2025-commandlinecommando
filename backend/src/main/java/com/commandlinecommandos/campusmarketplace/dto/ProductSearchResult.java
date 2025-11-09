package com.commandlinecommandos.campusmarketplace.dto;

import com.commandlinecommandos.campusmarketplace.model.ProductCategory;
import com.commandlinecommandos.campusmarketplace.model.ProductCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Product search result DTO
 * Represents a single product in search results with relevance score
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResult {
    
    /**
     * Product unique identifier
     */
    private UUID productId;
    
    /**
     * Product title
     */
    private String title;
    
    /**
     * Product description
     */
    private String description;
    
    /**
     * Product price
     */
    private BigDecimal price;
    
    /**
     * Product category
     */
    private ProductCategory category;
    
    /**
     * Product condition
     */
    private ProductCondition condition;
    
    /**
     * Seller information
     */
    private UUID sellerId;
    private String sellerName;
    private String sellerUsername;
    
    /**
     * Product location (pickup location)
     */
    private String location;
    
    /**
     * View count
     */
    private Integer viewCount;
    
    /**
     * Favorite count
     */
    private Integer favoriteCount;
    
    /**
     * Product creation timestamp
     */
    private LocalDateTime createdAt;
    
    /**
     * Product images (URLs or paths)
     */
    private List<String> imageUrls;
    
    /**
     * Search relevance score (from ts_rank)
     * Higher score = more relevant to search query
     */
    private Float relevanceScore;
    
    /**
     * Whether product is negotiable
     */
    private Boolean negotiable;
    
    /**
     * Quantity available
     */
    private Integer quantity;
}

