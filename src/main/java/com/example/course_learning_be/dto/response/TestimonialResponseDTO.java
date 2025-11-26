package com.example.course_learning_be.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestimonialResponseDTO {
  private String comment;
  private String userFullName;
  private String courseName;
}
