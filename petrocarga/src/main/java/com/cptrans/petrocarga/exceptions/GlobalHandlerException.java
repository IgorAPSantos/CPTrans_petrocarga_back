package com.cptrans.petrocarga.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

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
        error.put("erro", ex.getAllErrors().getFirst().getDefaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


}
