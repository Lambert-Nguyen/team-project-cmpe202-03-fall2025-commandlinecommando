package com.commandlinecommandos.campusmarketplace.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Product View entity
 * Tracks product views for recently viewed items and recommendation features
 * One view per user per product per day (unique constraint)
 */
@Entity
@Table(
    name = "product_views",
    indexes = {
        @Index(name = "idx_product_views_user", columnList = "user_id,viewed_at"),
        @Index(name = "idx_product_views_product", columnList = "product_id,viewed_at"),
        @Index(name = "idx_product_views_user_product", columnList = "user_id,product_id,viewed_at"),
        @Index(name = "idx_product_views_date", columnList = "viewed_at_date")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uniq_user_product_view_per_day",
            columnNames = {"user_id", "product_id", "viewed_at_date"}
        )
    }
)
public class ProductView {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;
    
    @Column(name = "viewed_at_date", nullable = false)
    private LocalDate viewedAtDate;
    
    public ProductView() {
    }
    
    /**
     * Set default values before persisting
     */
    @PrePersist
    protected void onCreate() {
        if (viewedAt == null) {
            viewedAt = LocalDateTime.now();
        }
        if (viewedAtDate == null) {
            viewedAtDate = LocalDate.now();
        }
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public LocalDateTime getViewedAt() {
        return viewedAt;
    }
    
    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }
    
    public LocalDate getViewedAtDate() {
        return viewedAtDate;
    }
    
    public void setViewedAtDate(LocalDate viewedAtDate) {
        this.viewedAtDate = viewedAtDate;
    }
}
