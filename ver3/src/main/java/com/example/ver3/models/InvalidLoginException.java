package com.example.ver3.models;

public class InvalidLoginException extends Exception{
    public InvalidLoginException(String message){
        super(message);
    }
}
