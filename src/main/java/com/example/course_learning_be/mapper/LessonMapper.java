package com.example.course_learning_be.mapper;

import com.example.course_learning_be.dto.request.LessonRequestDTO;
import com.example.course_learning_be.dto.response.LessonResponseDTO;
import com.example.course_learning_be.entity.Curriculum;
import com.example.course_learning_be.entity.Lesson;
import com.example.course_learning_be.entity.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LessonMapper {
  private final LessonMapperMS lessonMapperMS;
  public Lesson fromRequestDTOToEntity(LessonRequestDTO requestDTO, Curriculum curriculum, Video video) {
    Lesson entity = lessonMapperMS.toEntity(requestDTO);
    entity.setCurriculumId(curriculum.getId());
    entity.setVideoId(video.getId());
    return entity;

  }
  public LessonResponseDTO fromEntityToResponseDTO(Lesson entity) {
    return lessonMapperMS.toResponse(entity);
  }

  public LessonResponseDTO fromEntityToResponseDTOWithPosition(Lesson lesson, int position) {
    LessonResponseDTO responseDTO = lessonMapperMS.toResponse(lesson);
    responseDTO.setPosition(position);
    return responseDTO;
  }
  public void updateEntityFromRequestDTO(Lesson lesson, LessonRequestDTO requestDTO) {
    lessonMapperMS.doUpdate(lesson, requestDTO);

  }
}
