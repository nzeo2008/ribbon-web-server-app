package com.example.demo.repository;

import com.example.demo.entity.CommentEntity;
import com.example.demo.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findAllByPostEntity(PostEntity postEntity);

    Optional<CommentEntity> findCommentEntityByIdAndUserId(Long commentId,
                                                           Long userId);
}
