package com.commandlinecommandos.campusmarketplace.dto;

import com.commandlinecommandos.campusmarketplace.model.ProductCategory;
import com.commandlinecommandos.campusmarketplace.model.ProductCondition;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Product summary DTO for discovery features
 * Contains essential product information for trending/recommended items
 */
public class ProductSummary {
    
    private UUID productId;
    private String title;
    private String description;
    private BigDecimal price;
    private ProductCategory category;
    private ProductCondition condition;
    private List<String> imageUrls;
    private Integer viewCount;
    private Integer favoriteCount;
    private LocalDateTime createdAt;
    private UUID sellerId;
    private String sellerUsername;
    private String location;
    private Boolean negotiable;
    private Integer quantity;
    
    public ProductSummary() {
    }
    
    // Getters and Setters
    public UUID getProductId() {
        return productId;
    }
    
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public ProductCategory getCategory() {
        return category;
    }
    
    public void setCategory(ProductCategory category) {
        this.category = category;
    }
    
    public ProductCondition getCondition() {
        return condition;
    }
    
    public void setCondition(ProductCondition condition) {
        this.condition = condition;
    }
    
    public List<String> getImageUrls() {
        return imageUrls;
    }
    
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public Integer getFavoriteCount() {
        return favoriteCount;
    }
    
    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public UUID getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(UUID sellerId) {
        this.sellerId = sellerId;
    }
    
    public String getSellerUsername() {
        return sellerUsername;
    }
    
    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Boolean getNegotiable() {
        return negotiable;
    }
    
    public void setNegotiable(Boolean negotiable) {
        this.negotiable = negotiable;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
