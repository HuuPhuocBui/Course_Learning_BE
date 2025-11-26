package com.example.course_learning_be.mapper;

import com.example.course_learning_be.dto.request.CourseRequestDTO;
import com.example.course_learning_be.dto.response.CourseResponseDTO;
import com.example.course_learning_be.entity.Course;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
@Mapper(builder = @Builder(disableBuilder = true),
    uses = DTOMapper.class,
    componentModel = "spring")
public interface CourseMapperMS {
  @Mapping(target = "orderKeeper", ignore = true)
  @Mapping(target = "curriculums", ignore = true)
  Course toEntity(CourseRequestDTO dto);


  @Mapping(target = "totalLessons", ignore = true)
  @Mapping(target = "curriculums", ignore = true)
  @Mapping(source = "owner.fullName", target = "authorName")
  @Mapping(source = "owner.email", target = "ownerEmail")
  CourseResponseDTO toResponse(Course course);



  @Mapping(target = "orderKeeper", ignore = true)
  @Mapping(target = "curriculums", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void doUpdate(@MappingTarget Course course, CourseRequestDTO requestDTO);
}
