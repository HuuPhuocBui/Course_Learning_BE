package com.example.course_learning_be.dto.request;

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
public class LessonRequestDTO {
  private String title;

  private String videoId;

  private String duration;

  private int position;
}
