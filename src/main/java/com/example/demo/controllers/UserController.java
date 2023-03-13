package com.example.demo.controllers;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.facade.UserFacade;
import com.example.demo.services.UserService;
import com.example.demo.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/user")
@CrossOrigin
public class UserController {

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private UserFacade userFacade;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @GetMapping("/")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        UserEntity userEntity = userService.getCurrentUser(principal);
        UserDTO userDTO = userFacade.userToUserDTO(userEntity);

        return ResponseEntity.status(HttpStatus.OK)
                .body(userDTO);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("userId") String userId) {
        UserEntity userEntity = userService.getUserById(Long.parseLong(userId));
        UserDTO userDTO = userFacade.userToUserDTO(userEntity);

        return ResponseEntity.status(HttpStatus.OK)
                .body(userDTO);
    }

    @PostMapping("update")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDTO userDTO,
                                             BindingResult bindingResult,
                                             Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidatorService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        UserEntity userEntity = userService.updateUser(userDTO, principal);

        UserDTO updatedUser = userFacade.userToUserDTO(userEntity);

        logger.info("Пользователь c id: {} обновлён", updatedUser.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedUser);
    }
}
