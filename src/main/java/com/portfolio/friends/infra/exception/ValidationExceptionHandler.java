package com.portfolio.friends.infra.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestErrorMessage> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request,
            BindingResult result) {


        // Criar a resposta com os erros de validação
        RestErrorMessage errorMessage = new RestErrorMessage(
                request,
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Invalid fields",
                result
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}