package com.example.course_learning_be.service;

import com.example.course_learning_be.entity.General;
import com.example.course_learning_be.enums.WebConstant;
import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import com.example.course_learning_be.repository.GeneralRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneralService {
  private final GeneralRepository generalRepository;
  public General getSingletonGeneral() {
    General general = generalRepository.findTopByOrderByIdDesc();
    if (general == null) {
      general = new General();
      return generalRepository.save(general);
    }
    return general;
  }

  public String getHomepageVideoId() {
    General general = getSingletonGeneral();

    if (general.getVideoHomePageId() == null) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    return general.getVideoHomePageId();
  }

}
