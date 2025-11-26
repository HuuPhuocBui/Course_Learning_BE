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
public class ContactRequestDTO {
  private String fullName;
  private String email;
  private String phone;
  private String titleCourse;
  private String message;
}
