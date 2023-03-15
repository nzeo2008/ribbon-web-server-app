package com.example.demo.facade;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {
    public UserDTO userToUserDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userEntity.getId());
        userDTO.setFirstname(userEntity.getFirstname());
        userDTO.setLastname(userEntity.getLastname());
        userDTO.setUsername(userEntity.getUsername());
        userDTO.setUpdatedDate(userEntity.getUpdatedDate());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setCreatedDate(userEntity.getCreatedDate());

        return userDTO;
    }
}
