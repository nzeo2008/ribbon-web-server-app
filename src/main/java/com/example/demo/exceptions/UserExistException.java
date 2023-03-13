package com.example.demo.exceptions;

import org.slf4j.Logger;

public class UserExistException extends RuntimeException {
    public UserExistException(String message) {
        super(message);
    }
}
