package com.example.demo.services;

import com.example.demo.dto.PostDTO;
import com.example.demo.entity.ImageEntity;
import com.example.demo.entity.PostEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.exceptions.PostNotFoundException;
import com.example.demo.facade.PostFacade;
import com.example.demo.payload.response.PageablePostResponse;
import com.example.demo.repository.IImageRepository;
import com.example.demo.repository.IPostRepository;
import com.example.demo.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired
    private final IPostRepository postsRepository;
    @Autowired
    private final IUserRepository usersRepository;
    @Autowired
    private final IImageRepository imageRepository;
    @Autowired
    private PostFacade postFacade;

    @Autowired
    public PostService(IPostRepository postsRepository,
                       IUserRepository usersRepository,
                       IImageRepository imageRepository) {
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
        this.imageRepository = imageRepository;
    }

    public PostEntity createPost(PostDTO postDTO,
                                 Principal principal) {
        UserEntity userEntity = getUserByPrincipal(principal);
        PostEntity postEntity = new PostEntity();
        postEntity.setUserEntity(userEntity);
        postEntity.setCaption(postDTO.getCaption());
        postEntity.setLocation(postDTO.getLocation());
        postEntity.setTitle(postDTO.getTitle());
        postEntity.setLikes(0);

        return postsRepository.save(postEntity);
    }

    public PageablePostResponse getAllPosts(int pageNum,
                                            int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<PostEntity> posts = postsRepository.findAllByOrderByCreatedDateDesc(pageable);
        List<PostEntity> listOfPostEntities = posts.getContent();
        List<PostDTO> content = listOfPostEntities
                .stream()
                .map(postFacade::postToPostDTO)
                .collect(Collectors.toList());

        PageablePostResponse postsResponse = new PageablePostResponse();
        postsResponse.setContent(content);
        postsResponse.setPageNum(posts.getNumber());
        postsResponse.setPageSize(posts.getSize());
        postsResponse.setTotalElements(posts.getTotalElements());
        postsResponse.setTotalPages(posts.getTotalPages());
        postsResponse.setIsLastPage(posts.isLast());

        return postsResponse;
    }

    public PostEntity getPostByIdWithAuth(Long postId,
                                          Principal principal) {
        UserEntity userEntity = getUserByPrincipal(principal);
        return postsRepository.findPostsByIdAndUserEntity(postId, userEntity)
                .orElseThrow(() -> new PostNotFoundException("Пост не может быть найден для: " + userEntity.getEmail()));
    }

    public PostEntity getPostById(Long postId) {
        return postsRepository.findPostEntityById(postId)
                .orElseThrow(() -> new PostNotFoundException("Пост c id: " + postId + " не может быть найден"));
    }

    public List<PostEntity> getAllPostForUser(Principal principal) {
        UserEntity userEntity = getUserByPrincipal(principal);
        return postsRepository.findAllByUserEntityOrderByCreatedDateDesc(userEntity);
    }

    public PostEntity likePost(Long postId,
                               String username) {
        PostEntity postEntity = postsRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Пост не может быть найден"));

        Optional<String> userLiked = postEntity.getLikedUsers()
                .stream()
                .filter(user -> user.equals(username))
                .findAny();

        if (userLiked.isPresent()) {
            postEntity.setLikes(postEntity.getLikes() - 1);
            postEntity.getLikedUsers()
                    .remove(username);
        } else {
            postEntity.setLikes(postEntity.getLikes() + 1);
            postEntity.getLikedUsers()
                    .add(username);
        }
        return postsRepository.save(postEntity);
    }

    public void deletePost(Long postId,
                           Principal principal) {
        PostEntity postEntity = getPostByIdWithAuth(postId, principal);
        Optional<ImageEntity> imageModel = imageRepository.findByPostId(postEntity.getId());
        postsRepository.delete(postEntity);
        imageModel.ifPresent(imageRepository::delete);
    }

    private UserEntity getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return usersRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с никнеймом " + username + " не найден"));
    }

    public PostEntity updatePost(Long postId,
                                 PostDTO postDTO,
                                 Principal principal) {
        PostEntity postEntity = getPostByIdWithAuth(postId, principal);

        postEntity.setTitle(postDTO.getTitle());
        postEntity.setLocation(postDTO.getLocation());
        postEntity.setCaption(postDTO.getCaption());
        postEntity.setUpdatedDate(LocalDateTime.now());

        return postsRepository.save(postEntity);
    }
}
