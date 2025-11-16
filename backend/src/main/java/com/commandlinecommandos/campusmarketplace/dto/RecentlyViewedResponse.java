package com.commandlinecommandos.campusmarketplace.dto;

import java.util.List;

/**
 * Response wrapper for recently viewed products
 */
public class RecentlyViewedResponse {
    private List<ProductSummary> recentlyViewed;

    public RecentlyViewedResponse() {
    }

    public RecentlyViewedResponse(List<ProductSummary> recentlyViewed) {
        this.recentlyViewed = recentlyViewed;
    }

    public List<ProductSummary> getRecentlyViewed() {
        return recentlyViewed;
    }

    public void setRecentlyViewed(List<ProductSummary> recentlyViewed) {
        this.recentlyViewed = recentlyViewed;
    }
}

