package com.example.course_learning_be.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  INVALID_INPUT(1001, "Invalid input"),
  AUTHENTICATION(1002, "The user is not authorized to access")
  ;
  ErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  private final int code;
  private final String message;
}
