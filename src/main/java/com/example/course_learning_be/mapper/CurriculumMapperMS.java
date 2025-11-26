package com.example.course_learning_be.mapper;

import com.example.course_learning_be.dto.request.CurriculumRequestDTO;
import com.example.course_learning_be.dto.response.CurriculumResponseDTO;
import com.example.course_learning_be.entity.Curriculum;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
@Mapper(builder = @Builder(disableBuilder = true),
    uses = DTOMapper.class,
    componentModel = "spring")
public interface CurriculumMapperMS {
  @Mapping(target = "courseId", ignore = true)
  @Mapping(target = "orderKeeper", ignore = true)
  @Mapping(target = "lessons", ignore = true)
  Curriculum toEntity(CurriculumRequestDTO requestDTO);

  @Mapping(source = "id", target = "curriculumId")
  //@Mapping(target = "position", ignore = true)
  @Mapping(target = "lessonIds", ignore = true)
  CurriculumResponseDTO toResponse(Curriculum curriculum);


  //@Mapping(source = "position", target = "position", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void doUpdate(@MappingTarget Curriculum curriculum, CurriculumRequestDTO requestDTO);
}
