package com.commandlinecommandos.campusmarketplace.dto;

import java.util.List;

/**
 * Generic paginated response wrapper
 */
public class PagedResponse<T> {
    
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;

    // Constructors
    public PagedResponse() {
    }

    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last, boolean empty) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.empty = empty;
    }

    // Builder pattern - manual implementation
    public static <T> PagedResponseBuilder<T> builder() {
        return new PagedResponseBuilder<>();
    }

    public static class PagedResponseBuilder<T> {
        private final PagedResponse<T> response = new PagedResponse<>();

        public PagedResponseBuilder<T> content(List<T> content) {
            response.content = content;
            return this;
        }

        public PagedResponseBuilder<T> page(int page) {
            response.page = page;
            return this;
        }

        public PagedResponseBuilder<T> size(int size) {
            response.size = size;
            return this;
        }

        public PagedResponseBuilder<T> totalElements(long totalElements) {
            response.totalElements = totalElements;
            return this;
        }

        public PagedResponseBuilder<T> totalPages(int totalPages) {
            response.totalPages = totalPages;
            return this;
        }

        public PagedResponseBuilder<T> first(boolean first) {
            response.first = first;
            return this;
        }

        public PagedResponseBuilder<T> last(boolean last) {
            response.last = last;
            return this;
        }

        public PagedResponseBuilder<T> empty(boolean empty) {
            response.empty = empty;
            return this;
        }

        public PagedResponse<T> build() {
            return response;
        }
    }

    // Getters and Setters
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
