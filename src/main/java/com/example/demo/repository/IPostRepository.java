package com.example.demo.repository;

import com.example.demo.entity.PostEntity;
import com.example.demo.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findAllByUserEntityOrderByCreatedDateDesc(UserEntity userEntity);

    Optional<PostEntity> findPostEntityById(Long id);

    Page<PostEntity> findAllByOrderByCreatedDateDesc(Pageable pageable);

    Optional<PostEntity> findPostsByIdAndUserEntity(Long id,
                                                    UserEntity userEntity);

}
