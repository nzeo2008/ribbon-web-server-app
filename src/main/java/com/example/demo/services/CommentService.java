package com.example.demo.services;

import com.example.demo.dto.CommentDTO;
import com.example.demo.entity.CommentEntity;
import com.example.demo.entity.PostEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.exceptions.CommentNotFoundException;
import com.example.demo.exceptions.PostNotFoundException;
import com.example.demo.facade.CommentFacade;
import com.example.demo.facade.PageableFacade;
import com.example.demo.payload.response.PageableResponse;
import com.example.demo.repository.ICommentRepository;
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
public class CommentService {
    @Autowired
    private final ICommentRepository commentRepository;
    @Autowired
    private final IPostRepository postsRepository;
    @Autowired
    private final IUserRepository usersRepository;

    @Autowired
    private CommentFacade commentFacade;

    @Autowired
    private PageableFacade<CommentDTO, CommentEntity> pageableFacade;

    @Autowired
    public CommentService(ICommentRepository commentRepository,
                          IPostRepository postsRepository,
                          IUserRepository usersRepository) {
        this.commentRepository = commentRepository;
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
    }

    public CommentEntity saveComment(Long postId,
                                     CommentDTO commentDTO,
                                     Principal principal) {
        UserEntity userEntity = getUserByPrincipal(principal);
        PostEntity postEntity = postsRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Пост не может быть найден для: " + userEntity.getEmail()));

        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setPostEntity(postEntity);
        commentEntity.setUserId(userEntity.getId());
        commentEntity.setUsername(commentDTO.getUsername());
        commentEntity.setMessage(commentDTO.getMessage());

        return commentRepository.save(commentEntity);
    }

    public PageableResponse<CommentDTO> getAllCommentsForPost(Long postId,
                                                              int pageNum,
                                                              int pageSize) {
        PostEntity postEntity = postsRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Пост не может быть найден"));
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<CommentEntity> comments = commentRepository.findAllByPostEntity(postEntity, pageable);
        List<CommentEntity> listOfPostComments = comments.getContent();
        List<CommentDTO> content = listOfPostComments.stream()
                .map(commentFacade::commentToCommentDTO)
                .collect(Collectors.toList());

        PageableResponse<CommentDTO> pageableResponse = pageableFacade.pageableToDTO(comments, content);


        return pageableResponse;
    }


    public void deleteComment(Long commentId) {
        Optional<CommentEntity> comment = commentRepository.findById(commentId);
        comment.ifPresent(commentRepository::delete);
    }

    private UserEntity getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return usersRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с никнеймом " + username + " не найден"));
    }

    private CommentEntity getCommentById(Long commentId,
                                         Principal principal) {
        UserEntity userEntity = getUserByPrincipal(principal);

        return commentRepository.findCommentEntityByIdAndUserId(commentId, userEntity.getId())
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не может быть найден для: " + userEntity.getEmail()));
    }

    public CommentEntity updateComment(Long commentId,
                                       CommentDTO commentDTO,
                                       Principal principal) {
        CommentEntity commentEntity = getCommentById(commentId, principal);

        commentEntity.setMessage(commentDTO.getMessage());
        commentEntity.setUpdatedDate(LocalDateTime.now());

        return commentRepository.save(commentEntity);
    }
}
