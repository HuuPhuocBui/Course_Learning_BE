package com.example.course_learning_be.mapper;

import com.example.course_learning_be.dto.request.BenefitRequestDTO;
import com.example.course_learning_be.dto.response.BenefitResponseDTO;
import com.example.course_learning_be.entity.Benefit;
import com.example.course_learning_be.enums.Language;
import java.util.Map;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(builder = @Builder(disableBuilder = true),
    uses = DTOMapper.class,
    componentModel = "spring")
public interface BenefitMapperMS {
  @Mapping(target = "title", source = "title", qualifiedByName = "localize")
  @Mapping(target = "description", source = "description", qualifiedByName = "localize")
  BenefitResponseDTO<String> toLocalizeResponse(Benefit benefit);

  @Mapping(target = "title", source = "title")
  @Mapping(target = "description", source = "description")
  BenefitResponseDTO<Map<Language, String>> toResponse(Benefit benefit);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  Benefit toEntity(BenefitRequestDTO dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void doUpdate(@MappingTarget Benefit benefit, BenefitRequestDTO dto);


}
