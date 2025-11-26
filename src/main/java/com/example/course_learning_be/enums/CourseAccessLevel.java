package com.example.course_learning_be.enums;

import lombok.Getter;

@Getter
public enum CourseAccessLevel {
  PUBLIC("Everyone can watch"),
  PRIVATE("Only host and subscribed user can watch");

  private final String description;

  CourseAccessLevel(String description) {
    this.description = description;
  }
}
