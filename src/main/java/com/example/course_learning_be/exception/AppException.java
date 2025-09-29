package com.example.course_learning_be.exception;

public class AppException extends RuntimeException{
  private final int code;

  public AppException(ErrorCode error) {
    super(error.getMessage());
    this.code = error.getCode();
  }

  public int getCode() {
    return code;
  }

}
