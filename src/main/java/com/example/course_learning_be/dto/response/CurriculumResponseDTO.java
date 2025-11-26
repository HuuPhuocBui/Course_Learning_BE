package com.example.course_learning_be.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumResponseDTO {
  private String curriculumId;
  private String courseId;
  private String title;
  private String description;
  private int position;
  private List<String> lessonIds;
}
