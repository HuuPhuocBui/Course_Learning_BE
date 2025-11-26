package com.example.course_learning_be.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
public class VideoUploadRequestDTO {
  @NotEmpty(message = "Must include at least one language for title")
  private String title;

  @NotEmpty(message = "Must include at least one language for description")
  private String description;

  @Min(value = 1)
  @Builder.Default
  private int totalChunk = 0;


  private String name;   // tên file (vd: intro.mp4)
  private String type;   // MIME type (vd: video/mp4)
  private double size;
}
