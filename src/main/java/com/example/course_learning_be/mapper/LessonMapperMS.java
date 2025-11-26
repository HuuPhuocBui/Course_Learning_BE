package com.example.course_learning_be.mapper;

import com.example.course_learning_be.dto.request.LessonRequestDTO;
import com.example.course_learning_be.dto.response.LessonResponseDTO;
import com.example.course_learning_be.entity.Lesson;
import org.mapstruct.Builder; // ✅ Của MapStruct, dùng được trong @Mapper
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapping;

@Mapper(builder = @Builder(disableBuilder = true),
    uses = DTOMapper.class,
    componentModel = "spring")
public interface LessonMapperMS {
  @Mapping(target = "curriculumId", ignore = true)
  @Mapping(target = "videoId", ignore = true)
  Lesson toEntity(LessonRequestDTO requestDTO);

  @Mapping(source = "id", target = "lessonId")
  LessonResponseDTO toResponse(Lesson lesson);

  @Mapping(target = "position", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void doUpdate(@MappingTarget Lesson lesson, LessonRequestDTO requestDTO);
}
