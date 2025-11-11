package com.commandlinecommandos.campusmarketplace.dto;

import java.util.List;

/**
 * Response wrapper for similar products
 */
public class SimilarResponse {
    private List<ProductSummary> similar;

    public SimilarResponse() {
    }

    public SimilarResponse(List<ProductSummary> similar) {
        this.similar = similar;
    }

    public List<ProductSummary> getSimilar() {
        return similar;
    }

    public void setSimilar(List<ProductSummary> similar) {
        this.similar = similar;
    }
}

