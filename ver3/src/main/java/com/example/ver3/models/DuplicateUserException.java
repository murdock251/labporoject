package com.example.ver3.models;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message)
    {
        super(message);
    }
}
