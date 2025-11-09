package com.commandlinecommandos.campusmarketplace.dto;

import java.util.List;

/**
 * Search response DTO containing search results and metadata
 */
public class SearchResponse {
    
    private List<ProductSearchResult> results;
    private long totalResults;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private SearchMetadata metadata;
    
    public SearchResponse() {
    }
    
    public SearchResponse(List<ProductSearchResult> results, long totalResults, int totalPages, 
                         int currentPage, int pageSize, boolean hasNext, boolean hasPrevious, 
                         SearchMetadata metadata) {
        this.results = results;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
        this.metadata = metadata;
    }
    
    public List<ProductSearchResult> getResults() {
        return results;
    }
    
    public void setResults(List<ProductSearchResult> results) {
        this.results = results;
    }
    
    public long getTotalResults() {
        return totalResults;
    }
    
    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public boolean isHasNext() {
        return hasNext;
    }
    
    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
    
    public boolean isHasPrevious() {
        return hasPrevious;
    }
    
    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
    
    public SearchMetadata getMetadata() {
        return metadata;
    }
    
    public void setMetadata(SearchMetadata metadata) {
        this.metadata = metadata;
    }
}

