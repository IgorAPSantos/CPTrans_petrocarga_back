package com.cptrans.petrocarga.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", ex.getMessage());
        String causeMessage = "unknown";
        if (ex.getCause() != null && ex.getCause().toString() != null) {
            causeMessage = ex.getCause().getMessage();
        }
        error.put("cause", causeMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", ex.getMessage());
        String causeMessage = "unknown";
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            causeMessage = ex.getCause().getMessage();
        }
        error.put("cause", causeMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", "Erro interno no servidor");
        String causeMessage = "unknown";
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            causeMessage = ex.getCause().getMessage();
        }
        error.put("cause", causeMessage);
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", "Integridade de dados violada.");
        String causeMessage = ex.getMostSpecificCause().getMessage().split("Detalhe:")[0].trim();
        error.put("cause", causeMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> error = new HashMap<>();
         if (!ex.getAllErrors().isEmpty()) {
        error.put("erro", ex.getAllErrors().get(0).getDefaultMessage());
        } else {
            error.put("erro", "Erro de validação desconhecido");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> error = new HashMap<>();
        if (!ex.getConstraintViolations().isEmpty()) {
            error.put("erro", ex.getConstraintViolations().iterator().next().getMessage());
        } else {
            error.put("erro", "Erro de validação desconhecido");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", ex.getMessage());
        error.put("cause", "Credenciais inválidas fornecidas.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", "Acesso negado.");
        String causeMessage = "unknown";
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            causeMessage = ex.getCause().getMessage();
        }
        error.put("cause", causeMessage);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", ex.getMessage());
        String causeMessage = "unknown";
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            causeMessage = ex.getCause().getMessage();
        }
        error.put("cause", causeMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}   
