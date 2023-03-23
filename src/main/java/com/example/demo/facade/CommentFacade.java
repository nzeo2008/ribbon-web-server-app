package com.example.demo.facade;

import com.example.demo.dto.CommentDTO;
import com.example.demo.entity.CommentEntity;
import org.springframework.stereotype.Component;

@Component
public class CommentFacade {
    public CommentDTO commentToCommentDTO(CommentEntity commentEntity) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(commentEntity.getId());
        commentDTO.setUsername(commentEntity.getUsername());
        commentDTO.setMessage(commentEntity.getMessage());
        commentDTO.setUpdatedDate(commentEntity.getUpdatedDate());
        commentDTO.setCreatedDate(commentEntity.getCreatedDate());

        return commentDTO;
    }
}
