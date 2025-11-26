package com.example.course_learning_be.dto.response;

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
public class LessonLearningResponseDTO {
  private String lessonId;
  private String title;
  private int position;
  private VideoResponseDTO video;
  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class VideoResponseDTO {
    private String id;
    private String thumbnailUrl;
    private String videoUrl;
    private String duration;
  }
}
