package com.commandlinecommandos.campusmarketplace.service;

import com.commandlinecommandos.campusmarketplace.model.Product;
import com.commandlinecommandos.campusmarketplace.model.ProductView;
import com.commandlinecommandos.campusmarketplace.model.User;
import com.commandlinecommandos.campusmarketplace.repository.ProductRepository;
import com.commandlinecommandos.campusmarketplace.repository.ProductViewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for tracking product views
 * Handles asynchronous view tracking and recently viewed items
 */
@Service
@Slf4j
public class ProductViewService {
    
    @Autowired
    private ProductViewRepository productViewRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * Track a product view asynchronously
     * Upserts view record (one per user per product per day)
     * Also increments product view count
     * 
     * @param user The user viewing the product
     * @param product The product being viewed
     */
    @Async
    @Transactional
    public void trackView(User user, Product product) {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime now = LocalDateTime.now();
            
            // Check if view already exists today
            Optional<ProductView> existingView = productViewRepository
                .findByUserAndProductAndViewedAtDate(user, product, today);
            
            if (existingView.isPresent()) {
                // Update timestamp of existing view
                productViewRepository.updateViewTime(user, product, today, now);
                log.debug("Updated view timestamp for user {} on product {}", 
                         user.getUserId(), product.getProductId());
            } else {
                // Create new view record
                ProductView view = ProductView.builder()
                    .user(user)
                    .product(product)
                    .viewedAt(now)
                    .viewedAtDate(today)
                    .build();
                    
                productViewRepository.save(view);
                
                // Increment product view count
                product.incrementViewCount();
                productRepository.save(product);
                
                log.debug("Created new view record for user {} on product {}", 
                         user.getUserId(), product.getProductId());
            }
        } catch (Exception e) {
            log.error("Error tracking product view: user={}, product={}", 
                     user.getUserId(), product.getProductId(), e);
            // Don't throw exception - view tracking should not break product viewing
        }
    }
    
    /**
     * Get recently viewed products for a user
     * 
     * @param user The user
     * @param limit Maximum number of products to return
     * @return List of recently viewed products
     */
    public List<Product> getRecentlyViewedProducts(User user, int limit) {
        return productViewRepository.findRecentlyViewedByUser(user, PageRequest.of(0, limit));
    }
    
    /**
     * Get products frequently viewed together with a given product
     * Used for "customers also viewed" recommendations
     * 
     * @param productId The product ID
     * @param limit Maximum number of recommendations
     * @return List of product IDs
     */
    public List<UUID> getFrequentlyViewedTogether(UUID productId, int limit) {
        return productViewRepository.findFrequentlyViewedTogether(productId, limit);
    }
    
    /**
     * Get total view count for a product
     * 
     * @param product The product
     * @return Total view count
     */
    public long getViewCount(Product product) {
        return productViewRepository.countByProduct(product);
    }
}

