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
public class VideoUploadResponseDTO {
  private String videoId;

  private String title;

  private String description;

  private int totalChunk;

  @Builder.Default
  private boolean chunkCompleteness = false;
}
