package com.example.course_learning_be.mapper;

import com.example.course_learning_be.dto.request.CurriculumRequestDTO;
import com.example.course_learning_be.dto.response.CurriculumResponseDTO;
import com.example.course_learning_be.entity.Course;
import com.example.course_learning_be.entity.Curriculum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurriculumMapper {
  private final CurriculumMapperMS curriculumMapperMS;
  public Curriculum fromRequestDTOToEntity(CurriculumRequestDTO dto, Course course) {
    return Curriculum.builder()
        .title(dto.getTitle())
        .description(dto.getDescription())
        .position(dto.getPosition())
        .courseId(course.getId())
        .build();
  }

  public CurriculumResponseDTO fromEntityToResponseDTO(Curriculum curriculum) {
    return CurriculumResponseDTO.builder()
        .curriculumId(curriculum.getId())
        .courseId(curriculum.getCourseId())
        .title(curriculum.getTitle())
        .description(curriculum.getDescription())
        .position(curriculum.getPosition())
        .build();
  }
  public CurriculumResponseDTO fromEntityToResponseDTOWithPosition(Curriculum curriculum, int position) {
    CurriculumResponseDTO responseDTO = curriculumMapperMS.toResponse(curriculum);
    responseDTO.setPosition(curriculum.getPosition());
    return responseDTO;
  }
  public void updateEntityFromRequestDTO(Curriculum curriculum, CurriculumRequestDTO requestDTO) {
    curriculumMapperMS.doUpdate(curriculum, requestDTO);

  }
}
