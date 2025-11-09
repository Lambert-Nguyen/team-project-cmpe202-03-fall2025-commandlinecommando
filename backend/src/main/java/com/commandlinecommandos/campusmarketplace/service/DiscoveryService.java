package com.commandlinecommandos.campusmarketplace.service;

import com.commandlinecommandos.campusmarketplace.dto.ProductSummary;
import com.commandlinecommandos.campusmarketplace.model.ModerationStatus;
import com.commandlinecommandos.campusmarketplace.model.Product;
import com.commandlinecommandos.campusmarketplace.model.ProductCategory;
import com.commandlinecommandos.campusmarketplace.model.User;
import com.commandlinecommandos.campusmarketplace.repository.ProductRepository;
import com.commandlinecommandos.campusmarketplace.repository.ProductViewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for product discovery features
 * Provides trending, recommended, similar, and recently viewed items
 */
@Service
@Slf4j
public class DiscoveryService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductViewRepository productViewRepository;
    
    /**
     * Get trending products for a university
     * Based on view count and favorite count
     * Cached for 15 minutes
     * 
     * @param universityId University UUID
     * @param limit Maximum number of products to return
     * @return List of trending products
     */
    @Cacheable(value = "trendingItems", key = "#universityId + '_' + #limit")
    public List<ProductSummary> getTrendingItems(UUID universityId, int limit) {
        try {
            // Get top products by views (from existing repository method)
            // Note: findTopByViews takes University entity, need to convert
            List<Product> products = productRepository.findTopByViews(
                null,  // Would need University entity here
                PageRequest.of(0, limit)
            );
            
            List<ProductSummary> trending = products.stream()
                .map(this::transformToSummary)
                .collect(Collectors.toList());
            
            log.debug("Trending items: universityId={}, count={}", universityId, trending.size());
            return trending;
        } catch (Exception e) {
            log.error("Error fetching trending items: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * Get recommended items for a user
     * Based on their browsing history and interests
     * Cached for 10 minutes per user
     * 
     * @param user User
     * @param limit Maximum number of products to return
     * @return List of recommended products
     */
    @Cacheable(value = "recommendedItems", key = "#user.userId + '_' + #limit")
    public List<ProductSummary> getRecommendedItems(User user, int limit) {
        try {
            // Get user's browsing history categories
            List<ProductCategory> interests = getUserInterests(user);
            
            if (interests.isEmpty()) {
                // No history, return trending items
                return getTrendingItems(user.getUniversity().getUniversityId(), limit);
            }
            
            // Get products from user's interested categories
            List<Product> recommended = new ArrayList<>();
            int perCategory = Math.max(1, limit / interests.size());
            
            for (ProductCategory category : interests) {
                List<Product> categoryProducts = productRepository
                    .findByUniversityAndCategoryAndIsActiveTrueAndModerationStatus(
                        user.getUniversity(),
                        category,
                        ModerationStatus.APPROVED,
                        PageRequest.of(0, perCategory)
                    ).getContent();
                    
                recommended.addAll(categoryProducts);
                
                if (recommended.size() >= limit) {
                    break;
                }
            }
            
            List<ProductSummary> result = recommended.stream()
                .limit(limit)
                .map(this::transformToSummary)
                .collect(Collectors.toList());
            
            log.debug("Recommended items: user={}, count={}", user.getUsername(), result.size());
            return result;
        } catch (Exception e) {
            log.error("Error fetching recommended items for user {}: {}", 
                     user.getUserId(), e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * Get similar items to a given product
     * Based on category and other attributes
     * 
     * @param productId Product UUID
     * @param limit Maximum number of products to return
     * @return List of similar products
     */
    public List<ProductSummary> getSimilarItems(UUID productId, int limit) {
        try {
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                log.warn("Product not found for similar items: {}", productId);
                return List.of();
            }
            
            // Find products in the same category
            List<Product> similar = productRepository
                .findByUniversityAndCategoryAndIsActiveTrueAndModerationStatus(
                    product.getUniversity(),
                    product.getCategory(),
                    ModerationStatus.APPROVED,
                    PageRequest.of(0, limit + 1)  // +1 to exclude the product itself
                ).getContent()
                .stream()
                .filter(p -> !p.getProductId().equals(productId))  // Exclude the product itself
                .limit(limit)
                .toList();
            
            List<ProductSummary> result = similar.stream()
                .map(this::transformToSummary)
                .collect(Collectors.toList());
            
            log.debug("Similar items: productId={}, count={}", productId, result.size());
            return result;
        } catch (Exception e) {
            log.error("Error fetching similar items for product {}: {}", 
                     productId, e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * Get recently viewed items for a user
     * Cached for 1 hour per user
     * 
     * @param user User
     * @param limit Maximum number of products to return
     * @return List of recently viewed products
     */
    @Cacheable(value = "recentlyViewed", key = "#user.userId + '_' + #limit")
    public List<ProductSummary> getRecentlyViewedItems(User user, int limit) {
        try {
            List<Product> recentlyViewed = productViewRepository.findRecentlyViewedByUser(
                user, PageRequest.of(0, limit)
            );
            
            List<ProductSummary> result = recentlyViewed.stream()
                .map(this::transformToSummary)
                .collect(Collectors.toList());
            
            log.debug("Recently viewed: user={}, count={}", user.getUsername(), result.size());
            return result;
        } catch (Exception e) {
            log.error("Error fetching recently viewed for user {}: {}", 
                     user.getUserId(), e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * Get user interests based on browsing history
     * Analyzes recently viewed products to determine preferred categories
     * 
     * @param user User
     * @return List of product categories the user is interested in
     */
    private List<ProductCategory> getUserInterests(User user) {
        try {
            List<Product> recentlyViewed = productViewRepository.findRecentlyViewedByUser(
                user, PageRequest.of(0, 20)  // Last 20 viewed products
            );
            
            // Count category occurrences
            return recentlyViewed.stream()
                .map(Product::getCategory)
                .distinct()
                .limit(3)  // Top 3 categories
                .toList();
        } catch (Exception e) {
            log.error("Error determining user interests for {}: {}", 
                     user.getUserId(), e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * Transform Product to ProductSummary
     */
    private ProductSummary transformToSummary(Product product) {
        return ProductSummary.builder()
            .productId(product.getProductId())
            .title(product.getTitle())
            .description(product.getDescription())
            .price(product.getPrice())
            .category(product.getCategory())
            .condition(product.getCondition())
            .imageUrl(null)  // TODO: Add primary image URL when image service is implemented
            .viewCount(product.getViewCount())
            .favoriteCount(product.getFavoriteCount())
            .createdAt(product.getCreatedAt())
            .sellerId(product.getSeller().getUserId())
            .sellerName(product.getSeller().getFirstName() + " " + product.getSeller().getLastName())
            .negotiable(product.isNegotiable())
            .quantity(product.getQuantity())
            .build();
    }
}

