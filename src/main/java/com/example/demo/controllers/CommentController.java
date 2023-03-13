package com.example.demo.controllers;

import com.example.demo.dto.CommentDTO;
import com.example.demo.entity.CommentEntity;
import com.example.demo.facade.CommentFacade;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.services.CommentService;
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
@CrossOrigin
@RequestMapping("api/comment")
public class CommentController {
    public static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentFacade commentFacade;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @PostMapping("create/{postId}")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDTO commentDTO,
                                                @PathVariable("postId") String postId,
                                                BindingResult bindingResult,
                                                Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidatorService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        CommentEntity commentEntity = commentService.saveComment(Long.parseLong(postId), commentDTO, principal);
        CommentDTO createdComment = commentFacade.commentToCommentDTO(commentEntity);

        logger.info("Создан комментарий id: {} к посту с id: {}", createdComment.getId(), postId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdComment);
    }

    @GetMapping("all/{postId}")
    public ResponseEntity<List<CommentDTO>> getAllCommentsToPost(@PathVariable("postId") String postId) {

        List<CommentDTO> commentDTOList = commentService.getAllCommentsForPost(Long.parseLong(postId))
                .stream()
                .map(commentFacade::commentToCommentDTO)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                .body(commentDTOList);
    }

    @PostMapping("update/{commentId}")
    public ResponseEntity<Object> updateComment(@PathVariable("commentId") String commentId,
                                                @Valid @RequestBody CommentDTO commentDTO,
                                                BindingResult bindingResult,
                                                Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidatorService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        CommentEntity commentEntity = commentService.updateComment(Long.parseLong(commentId), commentDTO, principal);
        CommentDTO updatedComment = commentFacade.commentToCommentDTO(commentEntity);

        logger.info("Обновлён комментарий с id: {}", updatedComment.getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedComment);
    }

    @DeleteMapping("delete/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable("commentId") String commentId) {
        commentService.deleteComment(Long.parseLong(commentId));

        logger.info("Удалён комментарий с id: {}", commentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new MessageResponse("Пост был успешно удалён"));
    }
}
