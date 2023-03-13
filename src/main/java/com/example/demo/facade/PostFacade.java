package com.example.demo.facade;

import com.example.demo.dto.PostDTO;
import com.example.demo.entity.PostEntity;
import org.springframework.stereotype.Component;

@Component
public class PostFacade {
    public PostDTO postToPostDTO(PostEntity postEntity) {
        PostDTO postDTO = new PostDTO();
        postDTO.setUsername(postEntity.getUserEntity()
                .getUsername());
        postDTO.setId(postEntity.getId());
        postDTO.setCaption(postEntity.getCaption());
        postDTO.setLocation(postEntity.getLocation());
        postDTO.setTitle(postEntity.getTitle());
        postDTO.setLikes(postEntity.getLikes());
        postDTO.setUsersLiked(postEntity.getLikedUsers());
        postDTO.setUpdatedDate(postEntity.getUpdatedDate());

        return postDTO;
    }
}
