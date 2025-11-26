package com.example.course_learning_be.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chunk {
  private long index;

  private String url;

  private String length;

  private String thumbnailUrl;
  private double chunkSize;
}
