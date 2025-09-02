package com.bi.assistant.exception;

import com.bi.assistant.dto.QueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(QueryGenerationException.class)
    public ResponseEntity<QueryResponse> handleQueryGenerationException(QueryGenerationException ex) {
        log.error("Query generation error: {}", ex.getMessage(), ex);
        QueryResponse response = new QueryResponse();
        response.setSuccess(false);
        response.setMessage("Failed to generate SQL query: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(QueryExecutionException.class)
    public ResponseEntity<QueryResponse> handleQueryExecutionException(QueryExecutionException ex) {
        log.error("Query execution error: {}", ex.getMessage(), ex);
        QueryResponse response = new QueryResponse();
        response.setSuccess(false);
        response.setMessage("Failed to execute query: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<QueryResponse> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        QueryResponse response = new QueryResponse();
        response.setSuccess(false);
        response.setMessage("Validation failed: " + errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<QueryResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        QueryResponse response = new QueryResponse();
        response.setSuccess(false);
        response.setMessage("An unexpected error occurred. Please try again.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
