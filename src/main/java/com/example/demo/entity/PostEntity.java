package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String caption;
    private String location;
    private Integer likes;

    @ElementCollection(targetClass = String.class)
    private Set<String> likedUsers = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;
    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, mappedBy = "postEntity", orphanRemoval = true)
    private List<CommentEntity> commentEntities = new ArrayList<>();
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Column()
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }
}
