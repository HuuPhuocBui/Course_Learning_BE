package com.example.course_learning_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseResponse {
  private String id;
  private String title;
  private String description;
  private String duration;
  private String level;
  private String authorName;
  private String price;
  private String imageUrl;
}
