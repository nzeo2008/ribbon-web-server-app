package com.example.demo.services;

import com.example.demo.entity.ImageEntity;
import com.example.demo.entity.PostEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.exceptions.ImageNotFoundException;
import com.example.demo.repository.IImageRepository;
import com.example.demo.repository.IPostRepository;
import com.example.demo.repository.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageUploadService {
    public static final Logger logger = LoggerFactory.getLogger(ImageUploadService.class);
    private final IImageRepository imageRepository;
    private final IUserRepository usersRepository;
    private final IPostRepository postsRepository;

    @Autowired
    public ImageUploadService(IImageRepository imageRepository,
                              IUserRepository usersRepository,
                              IPostRepository postsRepository) {
        this.imageRepository = imageRepository;
        this.usersRepository = usersRepository;
        this.postsRepository = postsRepository;
    }

    private static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException ex) {
            logger.error("Ошибка при декомпрессии изображения");
        }
        return outputStream.toByteArray();
    }

    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException ex) {
            logger.error("Ошибка при сжатии изображения");
        }

        return outputStream.toByteArray();
    }

    public ImageEntity uploadImageToUser(MultipartFile file,
                                         Principal principal)
            throws IOException {
        UserEntity userEntity = getUserByPrincipal(principal);
        logger.info("Загрузка изображения пользователю user: {}", userEntity.getUsername());

        ImageEntity userProfileImage = imageRepository.findByUserId(userEntity.getId())
                .orElse(null);
        if (!ObjectUtils.isEmpty(userProfileImage)) {
            imageRepository.delete(userProfileImage);
        }

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setUserId(userEntity.getId());
        imageEntity.setImageBytes(compressBytes(file.getBytes()));
        imageEntity.setName(file.getOriginalFilename());
        return imageRepository.save(imageEntity);
    }

    public ImageEntity uploadImageToPost(MultipartFile file,
                                         Principal principal,
                                         Long postId)
            throws IOException {
        UserEntity userEntity = getUserByPrincipal(principal);
        PostEntity postEntity = userEntity.getPostEntities()
                .stream()
                .filter(p -> p.getId()
                        .equals(postId))
                .collect(toSinglePostCollector());

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setUserId(userEntity.getId());
        imageEntity.setImageBytes(compressBytes(file.getBytes()));
        imageEntity.setName(file.getOriginalFilename());
        imageEntity.setPostId(postId);
        logger.info("Загрузка изображения к посту с id: {}", postEntity.getId());

        return imageRepository.save(imageEntity);
    }

    public ImageEntity getImageToUser(Principal principal) {
        UserEntity userEntity = getUserByPrincipal(principal);

        ImageEntity imageEntity = imageRepository.findByUserId(userEntity.getId())
                .orElse(null);
        if (!ObjectUtils.isEmpty(imageEntity)) {
            imageEntity.setImageBytes(decompressBytes(imageEntity.getImageBytes()));
        }

        return imageEntity;
    }

    public ImageEntity getImageToPost(Long postId) {
        ImageEntity imageEntity = imageRepository
                .findByPostId(postId)
                .orElseThrow(() -> new ImageNotFoundException("Невозможно найти изображение к посту с id: " + postId));

        if (!ObjectUtils.isEmpty(imageEntity)) {
            imageEntity.setImageBytes(decompressBytes(imageEntity.getImageBytes()));
        }

        return imageEntity;
    }

    private UserEntity getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return usersRepository
                .findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с никнеймом " + username + " не найден"));
    }

    private <T> Collector<T, ?, T> toSinglePostCollector() {
        return Collectors.collectingAndThen(Collectors.toList(), list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }
}
