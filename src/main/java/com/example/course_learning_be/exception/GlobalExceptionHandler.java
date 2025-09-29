package com.example.course_learning_be.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(AppException.class)
  public ResponseEntity<Map<String, Object>> handleAppException(AppException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("message", ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleOtherException(Exception ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("message", "Unexpected error: " + ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
