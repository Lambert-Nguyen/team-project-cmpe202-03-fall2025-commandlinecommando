package com.commandlinecommandos.campusmarketplace.repository;

import com.commandlinecommandos.campusmarketplace.model.Product;
import com.commandlinecommandos.campusmarketplace.model.ProductView;
import com.commandlinecommandos.campusmarketplace.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ProductView entity
 * Handles product view tracking and recently viewed items
 */
@Repository
public interface ProductViewRepository extends JpaRepository<ProductView, UUID> {
    
    /**
     * Find existing view for user and product on a specific date
     * Used to check if view already exists today
     */
    Optional<ProductView> findByUserAndProductAndViewedAtDate(
        User user, Product product, LocalDate date
    );
    
    /**
     * Find recently viewed products by user
     * @param user The user
     * @param pageable Pagination settings
     * @return List of recently viewed products
     */
    @Query("SELECT DISTINCT pv.product FROM ProductView pv " +
           "WHERE pv.user = :user " +
           "ORDER BY pv.viewedAt DESC")
    List<Product> findRecentlyViewedByUser(@Param("user") User user, Pageable pageable);
    
    /**
     * Update view timestamp for existing view
     * @param user The user
     * @param product The product
     * @param now Current timestamp
     */
    @Modifying
    @Query("UPDATE ProductView pv SET pv.viewedAt = :now " +
           "WHERE pv.user = :user AND pv.product = :product AND pv.viewedAtDate = :date")
    void updateViewTime(@Param("user") User user, 
                       @Param("product") Product product,
                       @Param("date") LocalDate date,
                       @Param("now") LocalDateTime now);
    
    /**
     * Count total views for a product
     */
    long countByProduct(Product product);
    
    /**
     * Get products frequently viewed together (for recommendations)
     * @param productId The product ID
     * @param limit Maximum number of results
     * @return List of product IDs frequently viewed with this product
     */
    @Query(value = 
        "SELECT pv2.product_id, COUNT(*) as co_view_count " +
        "FROM product_views pv1 " +
        "JOIN product_views pv2 ON pv1.user_id = pv2.user_id " +
        "    AND pv1.product_id != pv2.product_id " +
        "    AND ABS(EXTRACT(EPOCH FROM (pv1.viewed_at - pv2.viewed_at))) < 3600 " +
        "WHERE pv1.product_id = :productId " +
        "GROUP BY pv2.product_id " +
        "ORDER BY co_view_count DESC " +
        "LIMIT :limit",
        nativeQuery = true)
    List<UUID> findFrequentlyViewedTogether(@Param("productId") UUID productId, 
                                            @Param("limit") int limit);
}

