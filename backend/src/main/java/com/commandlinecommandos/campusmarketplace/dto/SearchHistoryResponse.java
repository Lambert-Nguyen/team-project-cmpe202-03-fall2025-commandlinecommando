package com.commandlinecommandos.campusmarketplace.dto;

import com.commandlinecommandos.campusmarketplace.model.SearchHistory;
import java.util.List;

/**
 * Response wrapper for search history
 */
public class SearchHistoryResponse {
    private List<SearchHistory> history;

    public SearchHistoryResponse() {
    }

    public SearchHistoryResponse(List<SearchHistory> history) {
        this.history = history;
    }

    public List<SearchHistory> getHistory() {
        return history;
    }

    public void setHistory(List<SearchHistory> history) {
        this.history = history;
    }
}

