package com.example.demo.payload.response;

import lombok.Getter;

@Getter
public class InvalidLoginResponse {

    private String email;
    private String password;

    public InvalidLoginResponse() {
        this.email = "Wrong email";
        this.password = "Wrong password";
    }
}
