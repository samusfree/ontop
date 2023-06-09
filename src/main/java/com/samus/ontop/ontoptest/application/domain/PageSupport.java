package com.samus.ontop.ontoptest.application.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

public class PageSupport<T> {
    public static final String FIRST_PAGE_NUM = "0";
    public static final String DEFAULT_PAGE_SIZE = "20";

    @Getter
    List<T> content;
    int pageNumber;
    int pageSize;
    long totalElements;

    public PageSupport() {
    }

    public PageSupport(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;

    }

    @JsonProperty
    public long totalPages() {
        return pageSize > 0 ? (totalElements - 1) / pageSize + 1 : 0;
    }

    @JsonProperty
    public boolean first() {
        return pageNumber == Integer.parseInt(FIRST_PAGE_NUM);
    }

    @JsonProperty
    public boolean last() {
        return (long) (pageNumber + 1) * pageSize >= totalElements;
    }
}
