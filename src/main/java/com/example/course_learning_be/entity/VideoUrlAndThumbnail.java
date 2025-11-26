package com.example.course_learning_be.entity;

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
public class VideoUrlAndThumbnail {
  private String videoUrl;
  private String thumbnailUrl;
  private String duration;
  private String publicId;
}
