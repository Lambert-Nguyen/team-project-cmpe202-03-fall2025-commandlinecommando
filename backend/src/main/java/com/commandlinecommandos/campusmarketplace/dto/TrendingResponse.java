package com.commandlinecommandos.campusmarketplace.dto;

import java.util.List;

/**
 * Response wrapper for trending products
 */
public class TrendingResponse {
    private List<ProductSummary> trending;

    public TrendingResponse() {
    }

    public TrendingResponse(List<ProductSummary> trending) {
        this.trending = trending;
    }

    public List<ProductSummary> getTrending() {
        return trending;
    }

    public void setTrending(List<ProductSummary> trending) {
        this.trending = trending;
    }
}

