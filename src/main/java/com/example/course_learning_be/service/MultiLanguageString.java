package com.example.course_learning_be.service;

import com.example.course_learning_be.enums.Language;
import java.util.HashMap;
import java.util.Map;

public class MultiLanguageString {
  private Map<Language, String> values = new HashMap<>();

  public MultiLanguageString() {
  }

  public MultiLanguageString(Map<Language, String> values) {
    if (values != null) {
      this.values.putAll(values);
    }
  }

  public String get(Language lang) {
    return values.get(lang);
  }

  public void set(Language lang, String value) {
    values.put(lang, value);
  }

  public Map<Language, String> getValues() {
    return values;
  }

  public void setValues(Map<Language, String> values) {
    this.values = values;
  }

  @Override
  public String toString() {
    return "MultiLanguageString{" +
        "values=" + values +
        '}';
  }
}
