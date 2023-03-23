package com.example.demo.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class PageableResponse<D> {
    private List<D> content;
    private Integer pageNum;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private Boolean isLastPage;
}
