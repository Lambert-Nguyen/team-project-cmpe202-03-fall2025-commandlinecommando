package com.commandlinecommandos.campusmarketplace.dto;

/**
 * Metadata about the search query execution
 * Provides information about search performance and applied filters
 */
public class SearchMetadata {
    
    private long searchTimeMs;
    private String appliedFilters;
    private int totalFilters;
    private String sortedBy;
    private boolean cached;
    private String searchQuery;
    
    public SearchMetadata() {
    }
    
    public SearchMetadata(long searchTimeMs, String appliedFilters, int totalFilters,
                         String sortedBy, boolean cached, String searchQuery) {
        this.searchTimeMs = searchTimeMs;
        this.appliedFilters = appliedFilters;
        this.totalFilters = totalFilters;
        this.sortedBy = sortedBy;
        this.cached = cached;
        this.searchQuery = searchQuery;
    }
    
    public long getSearchTimeMs() {
        return searchTimeMs;
    }
    
    public void setSearchTimeMs(long searchTimeMs) {
        this.searchTimeMs = searchTimeMs;
    }
    
    public String getAppliedFilters() {
        return appliedFilters;
    }
    
    public void setAppliedFilters(String appliedFilters) {
        this.appliedFilters = appliedFilters;
    }
    
    public int getTotalFilters() {
        return totalFilters;
    }
    
    public void setTotalFilters(int totalFilters) {
        this.totalFilters = totalFilters;
    }
    
    public String getSortedBy() {
        return sortedBy;
    }
    
    public void setSortedBy(String sortedBy) {
        this.sortedBy = sortedBy;
    }
    
    public boolean isCached() {
        return cached;
    }
    
    public void setCached(boolean cached) {
        this.cached = cached;
    }
    
    public String getSearchQuery() {
        return searchQuery;
    }
    
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
}

