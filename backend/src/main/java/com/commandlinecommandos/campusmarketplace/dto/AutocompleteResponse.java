package com.commandlinecommandos.campusmarketplace.dto;

import java.util.List;

/**
 * Response wrapper for autocomplete suggestions
 */
public class AutocompleteResponse {
    private List<String> suggestions;

    public AutocompleteResponse() {
    }

    public AutocompleteResponse(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}

