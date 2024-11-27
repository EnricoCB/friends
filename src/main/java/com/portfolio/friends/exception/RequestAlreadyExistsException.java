package com.portfolio.friends.exception;

public class RequestAlreadyExistsException extends RuntimeException{
    public RequestAlreadyExistsException(String message) {
        super(message);
    }
}
