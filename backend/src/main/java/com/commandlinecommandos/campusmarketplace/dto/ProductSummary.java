package com.commandlinecommandos.campusmarketplace.dto;

import com.commandlinecommandos.campusmarketplace.model.ProductCategory;
import com.commandlinecommandos.campusmarketplace.model.ProductCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Product summary DTO for discovery features
 * Contains essential product information for trending/recommended items
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummary {
    
    private UUID productId;
    private String title;
    private String description;
    private BigDecimal price;
    private ProductCategory category;
    private ProductCondition condition;
    private String imageUrl;  // First/primary image
    private Integer viewCount;
    private Integer favoriteCount;
    private LocalDateTime createdAt;
    private UUID sellerId;
    private String sellerName;
    private Boolean negotiable;
    private Integer quantity;
}

