package com.example.demo.repository;

import com.example.demo.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IImageRepository extends JpaRepository<ImageEntity, Long> {
    Optional<ImageEntity> findByUserId(Long userId);

    Optional<ImageEntity> findByPostId(Long postId);
}
