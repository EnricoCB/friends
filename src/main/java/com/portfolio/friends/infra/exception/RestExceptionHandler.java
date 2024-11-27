package com.portfolio.friends.infra.exception;

import com.portfolio.friends.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class RestExceptionHandler{

    @ExceptionHandler(FriendshipAlreadyExistsException.class)
    private ResponseEntity<RestErrorMessage> friendshipAlreadyExistsException(FriendshipAlreadyExistsException exception, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new RestErrorMessage(request, HttpStatus.CONFLICT, exception.getMessage()));
    }

    @ExceptionHandler(PrivateProfileException.class)
    private ResponseEntity<RestErrorMessage> privateProfileHandler(PrivateProfileException exception, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RestErrorMessage(request, HttpStatus.FORBIDDEN, exception.getMessage()));
    }

    @ExceptionHandler(RequestAlreadyExistsException.class)
    private ResponseEntity<RestErrorMessage> requestAlreadyExistsException(RequestAlreadyExistsException exception, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new RestErrorMessage(request, HttpStatus.CONFLICT, exception.getMessage()));
    }

    @ExceptionHandler({RequestNotFoundException.class, FriendshipNotFoundException.class, UserNotFoundException.class})
    private ResponseEntity<RestErrorMessage> requestNotFoundException(RuntimeException exception, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RestErrorMessage(request, HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<RestErrorMessage> methodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request, BindingResult result){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new RestErrorMessage(request, HttpStatus.UNPROCESSABLE_ENTITY, "invalid fields", result));
    }



}
