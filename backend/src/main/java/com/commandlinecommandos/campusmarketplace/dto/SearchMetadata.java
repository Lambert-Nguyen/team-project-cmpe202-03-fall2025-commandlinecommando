package com.commandlinecommandos.campusmarketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metadata about the search query execution
 * Provides information about search performance and applied filters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMetadata {
    
    /**
     * Search execution time in milliseconds
     */
    private long searchTimeMs;
    
    /**
     * Human-readable description of applied filters
     */
    private String appliedFilters;
    
    /**
     * Number of filters applied
     */
    private int totalFilters;
    
    /**
     * Sort criteria applied
     */
    private String sortedBy;
    
    /**
     * Whether results were served from cache
     */
    private boolean cached;
    
    /**
     * Search query that was executed
     */
    private String searchQuery;
}

