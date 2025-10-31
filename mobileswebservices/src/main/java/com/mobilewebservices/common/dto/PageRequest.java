package com.mobilewebservices.common.dto;

import jakarta.validation.constraints.Min;

public class PageRequest {

    @Min(value = 0, message = "Page number must be non-negative")
    private int page = 0;

    @Min(value = 1, message = "Page size must be positive")
    private int size = 20;

    private String sortBy = "id";
    private String sortDirection = "ASC";

    // Constructors
    public PageRequest() {}

    public PageRequest(int page, int size, String sortBy, String sortDirection) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    // Getter and Setter methods
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }

    // Utility methods
    public int getOffset() {
        return page * size;
    }

    public int getLimit() {
        return size;
    }

    public String getSortClause() {
        return sortBy + " " + sortDirection;
    }
}
