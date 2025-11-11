package com.commandlinecommandos.campusmarketplace.dto;

import java.util.List;

/**
 * Response wrapper for recommended products
 */
public class RecommendedResponse {
    private List<ProductSummary> recommended;

    public RecommendedResponse() {
    }

    public RecommendedResponse(List<ProductSummary> recommended) {
        this.recommended = recommended;
    }

    public List<ProductSummary> getRecommended() {
        return recommended;
    }

    public void setRecommended(List<ProductSummary> recommended) {
        this.recommended = recommended;
    }
}

