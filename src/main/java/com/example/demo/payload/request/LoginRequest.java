package com.example.demo.payload.request;

import com.example.demo.annotations.ValidEmail;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotEmpty(message = "Email не может быть пустым")
    @ValidEmail
    private String email;
    @NotEmpty(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль не может быть меньше 8 символов")
    @Size(max = 25, message = "Пароль не может быть больше 25 символов")
    private  String password;
}
