package com.example.demo.payload.request;

import com.example.demo.annotations.PasswordMatches;
import com.example.demo.annotations.ValidEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatches
public class SignupRequest {
    @Email(message = "Неверный email")
    @NotBlank(message = "Email обязателен")
    @ValidEmail
    private String email;
    @NotEmpty(message = "Введите имя")
    private String firstname;
    @NotEmpty(message = "Введите фамилию")
    private String lastname;
    @NotEmpty(message = "Введите никнейм")
    private String username;
    @NotEmpty(message = "Введите пароль")
    @Size(min = 8, message = "Пароль не может быть меньше 8 символов")
    @Size(max = 25, message = "Пароль не может быть больше 25 символов")
    private String password;
    @NotEmpty(message = "Введите подтверждение пароля")
    @Size(min = 8, message = "Пароль не может быть меньше 8 символов")
    @Size(max = 25, message = "Пароль не может быть больше 25 символов")
    private String confirmPassword;
}
