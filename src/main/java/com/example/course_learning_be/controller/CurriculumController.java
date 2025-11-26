package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.request.CurriculumRequestDTO;
import com.example.course_learning_be.dto.request.LessonRequestDTO;
import com.example.course_learning_be.dto.response.LessonResponseDTO;
import com.example.course_learning_be.service.CurriculumService;
import com.example.course_learning_be.service.LessonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/curriculum")
@Validated
@Slf4j(topic = "CURRICULUM-CONTROLLER")
@RequiredArgsConstructor
public class CurriculumController {
  private final LessonService lessonService;
  private final CurriculumService curriculumService;

  @PostMapping("/{curriculumId}/lesson")
  public ResponseEntity<LessonResponseDTO> createLesson(@RequestBody LessonRequestDTO requestDTO,
      @PathVariable String curriculumId) {
    LessonResponseDTO res = lessonService.createSimple(requestDTO, curriculumId);

    return ResponseEntity.ok().body(res);

  }
  @PutMapping("/{curriculumId}")
  public ResponseEntity<?> updateCurriculum(@PathVariable String curriculumId, @RequestBody CurriculumRequestDTO requestDTO) throws JsonProcessingException {

    var res = curriculumService.update(curriculumId, requestDTO);
    return ResponseEntity.ok().body(res);
  }
}
