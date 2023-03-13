package com.example.demo.payload.response;

import com.example.demo.dto.PostDTO;
import lombok.Data;

import java.util.List;

@Data
public class PageablePostResponse {
    private List<PostDTO> content;
    private Integer pageNum;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private Boolean isLastPage;
}
