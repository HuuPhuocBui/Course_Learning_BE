package com.example.course_learning_be.mapper;

import com.example.course_learning_be.dto.request.CourseRequestDTO;
import com.example.course_learning_be.dto.response.CourseResponse;
import com.example.course_learning_be.dto.response.CourseResponseDTO;
import com.example.course_learning_be.dto.response.CourseResponseDTO.CurriculumDTO;
import com.example.course_learning_be.entity.Course;
import com.example.course_learning_be.entity.User;
import com.example.course_learning_be.enums.CourseAccessLevel;
import com.example.course_learning_be.enums.CourseLevel;
import com.example.course_learning_be.enums.Language;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseMapper {
  private final CourseMapperMS courseMapperMS;

  public Course fromCreateRequestToEntity(CourseRequestDTO dto, User user) {
    return Course.builder()
        .title(dto.getTitle())
        .description(dto.getDescription())
        .content(dto.getContent())
        .duration(dto.getDuration())
        .level(dto.getLevel() != null ? Enum.valueOf(CourseLevel.class, dto.getLevel()) : null)
        .accessLevel(dto.getAccessLevel() != null ? Enum.valueOf(CourseAccessLevel.class, dto.getAccessLevel()) : null)
        .language(dto.getLanguage() != null ? Enum.valueOf(Language.class, dto.getLanguage()) : null)
        .price(dto.getPrice())
        .pinImageUrl(dto.getPinImageUrl())
        .previewImageUrls(dto.getPreviewImageUrls())
        .owner(user)
        .build();
  }

  public CourseResponseDTO fromEntityToResponse(Course course) {
    return CourseResponseDTO.builder()
        .id(course.getId())
        .title(course.getTitle())
        .description(course.getDescription())
        .content(course.getContent())
        .duration(course.getDuration())
        .level(course.getLevel())
        .accessLevel(course.getAccessLevel())
        .language(course.getLanguage())
        .price(course.getPrice())
        .pinImageUrl(course.getPinImageUrl())
        .previewImageUrls(course.getPreviewImageUrls())
        .ownerEmail(course.getOwner() != null ? course.getOwner().getEmail() : null)
        .authorName(course.getOwner() != null ? course.getOwner().getFullName() : "Unknown Author")
        .build();
  }

  public static CourseResponse mapToCourseDto(Course course) {
    if (course == null) {
      return null;
    }

    return CourseResponse.builder()
        .id(course.getId())
        .title(course.getTitle())
        .description(course.getDescription())
        .duration(course.getDuration())
        .price(course.getPrice())
        .level(course.getLevel())
        .accessLevel(course.getAccessLevel())
        .pinImageUrl(course.getPinImageUrl())
        .previewImageUrls(course.getPreviewImageUrls())
        .ownerName(course.getOwner() != null ? course.getOwner().getFullName() : "Unknown")
        .build();
  }
  public void updateEntityFromRequestDTO(Course course, CourseRequestDTO requestDTO) {
    courseMapperMS.doUpdate(course, requestDTO);

  }

  public CourseResponseDTO fromEntityToCourseAndCurriculumResponseDTO(Course course, List<CurriculumDTO> list) {
    CourseResponseDTO responseDTO = courseMapperMS.toResponse(course);
    responseDTO.setAuthorName(course.getOwner().getFullName());
    responseDTO.setCurriculums(list);

    return responseDTO;
  }
}
