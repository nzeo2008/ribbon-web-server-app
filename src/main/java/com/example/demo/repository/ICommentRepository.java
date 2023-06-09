package com.example.demo.repository;

import com.example.demo.entity.CommentEntity;
import com.example.demo.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICommentRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findAllByPostEntityOrderById(PostEntity postEntity,
                                                     Pageable pageable);

    Optional<CommentEntity> findCommentEntityByIdAndUserId(Long commentId,
                                                           Long userId);

}
