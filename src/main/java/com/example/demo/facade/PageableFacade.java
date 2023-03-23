package com.example.demo.facade;

import com.example.demo.payload.response.PageableResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class PageableFacade<D, E> {
    public PageableResponse<D> pageableToDTO(Page<E> entity,
                                             List<D> content) {
        PageableResponse<D> pageableResponse = new PageableResponse<>();
        pageableResponse.setContent(content);
        pageableResponse.setPageNum(entity.getNumber());
        pageableResponse.setPageSize(entity.getSize());
        pageableResponse.setTotalElements(entity.getTotalElements());
        pageableResponse.setTotalPages(entity.getTotalPages());
        pageableResponse.setIsLastPage(entity.isLast());
        return pageableResponse;
    }
}