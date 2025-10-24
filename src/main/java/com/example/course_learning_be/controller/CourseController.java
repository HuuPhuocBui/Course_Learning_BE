package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.request.CourseRequest;
import com.example.course_learning_be.dto.response.ApiResponse;
import com.example.course_learning_be.dto.response.CourseResponse;
import com.example.course_learning_be.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController {

  private final CourseService courseService;

  @PostMapping("/AddCourse")
  ApiResponse<Boolean> addCourse(@RequestBody CourseRequest courseRequest) {
    var result = courseService.addCourse(courseRequest);
    return ApiResponse.<Boolean>builder()
        .data(result)
        .build();

  }
}
