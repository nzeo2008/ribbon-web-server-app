package com.example.demo.controllers;

import com.example.demo.dto.PostDTO;
import com.example.demo.entity.PostEntity;
import com.example.demo.facade.PostFacade;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.payload.response.PageableResponse;
import com.example.demo.services.PostService;
import com.example.demo.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/post")
@CrossOrigin
public class PostController {

    public static final Logger logger = LoggerFactory.getLogger(PostEntity.class);
    @Autowired
    private PostFacade postFacade;
    @Autowired
    private PostService postService;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @PostMapping("create")
    public ResponseEntity<Object> createPost(@Valid @RequestBody PostDTO postDTO,
                                             BindingResult bindingResult,
                                             Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidatorService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        PostEntity postEntity = postService.createPost(postDTO, principal);
        PostDTO createdPost = postFacade.postToPostDTO(postEntity);

        logger.info("Создан пост с id: {}", createdPost.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdPost);
    }

    @GetMapping("all")
    public ResponseEntity<PageableResponse<PostDTO>> getAllPosts(@RequestParam(value = "pageNum", defaultValue = "0", required = false) int pageNum,
                                                                 @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        PageableResponse<PostDTO> postsResponse = postService.getAllPosts(pageNum, pageSize);

        return ResponseEntity.status(HttpStatus.OK)
                .body(postsResponse);
    }

    @GetMapping("getPost/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable("postId") Long postId) {
        PostEntity postEntity = postService.getPostById(postId);

        PostDTO postDTO = postFacade.postToPostDTO(postEntity);
        return ResponseEntity.status(HttpStatus.OK)
                .body(postDTO);
    }

    @GetMapping("user/posts")
    public ResponseEntity<List<PostDTO>> getAllPostsForUser(Principal principal) {
        List<PostDTO> postDTOList = postService.getAllPostForUser(principal)
                .stream()
                .map(postFacade::postToPostDTO)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                .body(postDTOList);
    }


    @GetMapping("liked/posts")
    public ResponseEntity<List<PostDTO>> getLikedPosts(Principal principal) {
        List<PostDTO> postDTOList = postService.getLikedPosts(principal)
                .stream()
                .map(postFacade::postToPostDTO)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                .body(postDTOList);
    }

    @PostMapping("like/{postId}/{username}")
    public ResponseEntity<PostDTO> likePost(@PathVariable("postId") String postId,
                                            @PathVariable("username") String username) {
        PostEntity postEntity = postService.likePost(Long.parseLong(postId), username);
        PostDTO postDTO = postFacade.postToPostDTO(postEntity);

        return ResponseEntity.status(HttpStatus.OK)
                .body(postDTO);
    }

    @DeleteMapping("delete/{postId}")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable("postId") String postId,
                                                      Principal principal) {
        postService.deletePost(Long.parseLong(postId), principal);

        logger.info("Удалён пост с id: {}", postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new MessageResponse("Пост был успешно удалён"));
    }

    @PostMapping("update/{postId}")
    public ResponseEntity<Object> updatePost(@PathVariable("postId") String postId,
                                             @Valid @RequestBody PostDTO postDTO,
                                             BindingResult bindingResult,
                                             Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidatorService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        PostEntity postEntity = postService.updatePost(Long.parseLong(postId), postDTO, principal);
        PostDTO updatedPost = postFacade.postToPostDTO(postEntity);

        logger.info("Обновлён пост с id: {}", postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedPost);
    }
}
