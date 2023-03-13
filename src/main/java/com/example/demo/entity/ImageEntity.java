package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

@Data
@Entity
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column()
    private byte[] imageByte;
    @JsonIgnore
    private Long userId;
    @JsonIgnore
    private Long postId;
}
