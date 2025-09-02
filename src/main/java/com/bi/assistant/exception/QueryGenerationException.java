package com.bi.assistant.exception;

public class QueryGenerationException extends RuntimeException {
    public QueryGenerationException(String message) {
        super(message);
    }
    
    public QueryGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
