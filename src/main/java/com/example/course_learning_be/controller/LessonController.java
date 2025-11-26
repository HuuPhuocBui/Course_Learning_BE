package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.request.LessonRequestDTO;
import com.example.course_learning_be.service.LessonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lesson")
@Validated
@Slf4j(topic = "LESSON-CONTROLLER")
@RequiredArgsConstructor
public class LessonController {
  private final LessonService lessonService;

  @PutMapping("/{lessonId}")
  public ResponseEntity<?> updateLesson(@PathVariable String lessonId, @RequestBody LessonRequestDTO requestDTO) throws JsonProcessingException {

    var res = lessonService.update(requestDTO, lessonId);
    return ResponseEntity.ok().body(res);
  }
}
