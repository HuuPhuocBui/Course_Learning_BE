package com.example.course_learning_be.service;

import com.example.course_learning_be.enums.Language;
import java.util.Locale;

public class LocalizationService {
  public static Language getLanguage() {
    Locale locale = Locale.getDefault();
    String lang = locale.getLanguage().toLowerCase();

    return switch (lang) {
      case "vi" -> Language.VI;
      case "en" -> Language.EN;
      default -> Language.EN; // fallback
    };
  }
}
