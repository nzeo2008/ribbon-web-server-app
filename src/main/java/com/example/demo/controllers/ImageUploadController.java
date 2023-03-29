package com.example.demo.controllers;

import com.example.demo.entity.ImageEntity;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.services.ImageUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("api/image")
@CrossOrigin
public class ImageUploadController {
    public static final Logger logger = LoggerFactory.getLogger(ImageUploadController.class);

    @Autowired
    private ImageUploadService imageUploadService;

    @PostMapping("upload")
    public ResponseEntity<MessageResponse> uploadImageToUser(@RequestParam("file") MultipartFile file,
                                                             Principal principal)
            throws IOException {
        imageUploadService.uploadImageToUser(file, principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new MessageResponse("Изображение успешно загружено"));
    }

    @PostMapping("upload/{postId}")
    public ResponseEntity<MessageResponse> uploadImageToPost(@PathVariable("postId") String postId,
                                                             @RequestParam("file") MultipartFile file,
                                                             Principal principal)
            throws IOException {
        imageUploadService.uploadImageToPost(file, principal, Long.parseLong(postId));

        logger.info("Загружено изображение к посту с id: {}", postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new MessageResponse("Изображение успешно загружено"));

    }

    @GetMapping("profileImage")
    public ResponseEntity<ImageEntity> getImageForUser(Principal principal) {
        ImageEntity userImage = imageUploadService.getImageToUser(principal);

        return ResponseEntity.status(HttpStatus.OK)
                .body(userImage);
    }

    @GetMapping("getImage/{postId}")
    public ResponseEntity<ImageEntity> getImageToPost(@PathVariable("postId") String postId) {
        ImageEntity postImage = imageUploadService.getImageToPost(Long.parseLong(postId));

        return ResponseEntity.status(HttpStatus.OK)
                .body(postImage);
    }

}
