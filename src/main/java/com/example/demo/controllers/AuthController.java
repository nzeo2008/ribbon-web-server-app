package com.example.demo.controllers;

import com.example.demo.exceptions.UserNotExistException;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.payload.response.JWTTokenSuccessResponse;
import com.example.demo.services.UserService;
import com.example.demo.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("api/auth")
@PreAuthorize("permitAll()")
public class AuthController {
    public static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private ResponseErrorValidation responseErrorValidation;
    @Autowired
    private UserService userService;

    @PostMapping("login")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                                   BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidatorService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        if (!userService.findUserByEmail(loginRequest.getEmail()))
            throw new UserNotExistException("Пользователя с таким email: " + loginRequest.getEmail() + " не существует");

        String jwt = userService.getJWT(loginRequest);

        return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));
    }

    @PostMapping("register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signupRequest,
                                               BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidatorService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        userService.createUser(signupRequest);

        logger.info("Пользователь сохранён {}", signupRequest.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Пользователь создан");
    }

}
