package com.commandlinecommandos.campusmarketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Search response DTO containing search results and metadata
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    
    /**
     * List of product search results
     */
    private List<ProductSearchResult> results;
    
    /**
     * Total number of results matching the search criteria
     */
    private long totalResults;
    
    /**
     * Total number of pages available
     */
    private int totalPages;
    
    /**
     * Current page number (0-indexed)
     */
    private int currentPage;
    
    /**
     * Number of results per page
     */
    private int pageSize;
    
    /**
     * Whether there is a next page
     */
    private boolean hasNext;
    
    /**
     * Whether there is a previous page
     */
    private boolean hasPrevious;
    
    /**
     * Metadata about the search execution
     */
    private SearchMetadata metadata;
}

