package com.example.course_learning_be.mapper;

import com.example.course_learning_be.enums.Language;
import com.example.course_learning_be.service.LocalizationService;
import com.example.course_learning_be.service.MultiLanguageString;
import java.util.Map;
import java.util.function.Consumer;
import org.mapstruct.Named;

public abstract class DTOMapper {
  public static MultiLanguageString convertToMultiLanguageString(Map<Language, String> mp) {
    return new MultiLanguageString(mp);
  }

  @Named("localize")
  public static String getDataByLang(Map<Language, String> mp) {
    return mp.get(LocalizationService.getLanguage());
  }

  @Named("localize")
  public static <T> void applyIfNotNull(T value, Consumer<T> setter) {
    if (value != null) {
      setter.accept(value);
    }
  }
}
