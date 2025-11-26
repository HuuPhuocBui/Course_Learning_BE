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
public class VideoResponseDTO {
  private String id;
  private String thumbnailUrl;
  private String videoUrl;
  private String duration;
  private long totalChunk;
  private double totalSize;
}
