package io.github.matthewjacobsd.sanaspace.utils;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

// 📄 Maps Spring Page to frontend IPaginationResponse
@Data
public class PaginationResponse<T> {
    private int page;
    private int limit;
    private List<T> items;
    private long totalItems;
    private int totalPages;
    private boolean isLastPage;

    // Constructor to transform Page<T> to PaginationResponse<T>
    public PaginationResponse(Page<T> page) {
        this.page = page.getNumber();
        this.limit = page.getSize();
        this.items = page.getContent();
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.isLastPage = page.isLast();
    }
}