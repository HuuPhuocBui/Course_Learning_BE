package com.example.course_learning_be.enums;

public enum Language {
  EN,VI;


  public static boolean isValid(String lang) {
    try {
      Language.valueOf(lang.toUpperCase()); // kiểm tra có tồn tại không
      return true;
    } catch (IllegalArgumentException | NullPointerException e) {
      return false;
    }
  }
}
