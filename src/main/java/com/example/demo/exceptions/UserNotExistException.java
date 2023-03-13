package com.example.demo.exceptions;

import org.slf4j.Logger;

public class UserNotExistException extends RuntimeException {
    public UserNotExistException(String message) {
        super(message);
    }
}
