package com.commandlinecommandos.campusmarketplace.repository;

import com.commandlinecommandos.campusmarketplace.model.ModerationStatus;
import com.commandlinecommandos.campusmarketplace.model.Product;
import com.commandlinecommandos.campusmarketplace.model.ProductCategory;
import com.commandlinecommandos.campusmarketplace.model.ProductCondition;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA Specifications for dynamic Product filtering
 * Handles complex search criteria without native query limitations
 */
public class ProductSpecifications {
    
    /**
     * Build comprehensive search specification with all filters
     * 
     * @param universityId University UUID
     * @param categories List of categories to filter by (optional)
     * @param conditions List of conditions to filter by (optional)
     * @param minPrice Minimum price (optional)
     * @param maxPrice Maximum price (optional)
     * @param location Location search term (optional)
     * @param dateFrom Minimum creation date (optional)
     * @return Specification for filtering products
     */
    public static Specification<Product> withFilters(
            UUID universityId,
            List<ProductCategory> categories,
            List<ProductCondition> conditions,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String location,
            LocalDateTime dateFrom) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Base filters: active and approved products only
            predicates.add(criteriaBuilder.equal(root.get("isActive"), true));
            predicates.add(criteriaBuilder.equal(root.get("moderationStatus"), ModerationStatus.APPROVED));
            
            // University filter
            predicates.add(criteriaBuilder.equal(root.get("university").get("universityId"), universityId));
            
            // Category filter - IN clause for multiple categories
            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("category").in(categories));
            }
            
            // Condition filter - IN clause for multiple conditions
            if (conditions != null && !conditions.isEmpty()) {
                predicates.add(root.get("condition").in(conditions));
            }
            
            // Price range filters
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            
            // Location filter - case-insensitive like search
            if (location != null && !location.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("pickupLocation")),
                    "%" + location.toLowerCase() + "%"
                ));
            }
            
            // Date filter - products created after dateFrom
            if (dateFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), dateFrom));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Specification for filtering by university only
     * Used for base queries
     */
    public static Specification<Product> byUniversity(UUID universityId) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("university").get("universityId"), universityId);
    }
    
    /**
     * Specification for active products only
     */
    public static Specification<Product> isActive() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("isActive"), true);
    }
    
    /**
     * Specification for approved products only
     */
    public static Specification<Product> isApproved() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("moderationStatus"), ModerationStatus.APPROVED);
    }
    
    /**
     * Specification for filtering by category
     */
    public static Specification<Product> hasCategory(ProductCategory category) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("category"), category);
    }
    
    /**
     * Specification for filtering by condition
     */
    public static Specification<Product> hasCondition(ProductCondition condition) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("condition"), condition);
    }
    
    /**
     * Specification for price range
     */
    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

