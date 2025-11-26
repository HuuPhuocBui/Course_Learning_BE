package com.example.course_learning_be.mapper;

import com.example.course_learning_be.dto.request.BenefitRequestDTO;
import com.example.course_learning_be.dto.response.BenefitResponseDTO;
import com.example.course_learning_be.entity.Benefit;
import com.example.course_learning_be.enums.Language;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BenefitMapper {
  private final BenefitMapperMS benefitMapperMS;
  public BenefitResponseDTO<String> fromEntityToLocalizeResponse(Benefit benefit) {
    return benefitMapperMS.toLocalizeResponse(benefit);
  }
  public BenefitResponseDTO<Map<Language, String>> fromEntityToResponse(Benefit benefit) {
    return benefitMapperMS.toResponse(benefit);
  }

  public Benefit fromCreateRequestToEntity(BenefitRequestDTO dto) {
    return benefitMapperMS.toEntity(dto);
  }

  public void updateEntityFromRequestDTO(Benefit benefit, BenefitRequestDTO requestDTO) {
    benefitMapperMS.doUpdate(benefit, requestDTO);
  }

}
