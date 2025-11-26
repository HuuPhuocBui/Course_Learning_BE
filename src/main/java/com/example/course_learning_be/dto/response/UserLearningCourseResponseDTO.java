package com.example.course_learning_be.dto.response;

import com.example.course_learning_be.enums.CourseLevel;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UserLearningCourseResponseDTO {
  private long totalCourse;

  private List<CourseDTO> courses;

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @RequiredArgsConstructor
  public static class CourseDTO {
    private String id;
    private String title;
    private String duration;
    private CourseLevel level;
    private String authorName;
    private String pinImageUrl;
    private String description;

  }
}
