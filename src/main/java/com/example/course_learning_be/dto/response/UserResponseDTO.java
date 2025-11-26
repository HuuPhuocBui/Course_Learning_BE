package com.example.course_learning_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {

  private String id;
  private String email;
  private String avatar;
  private String avatarUrl;
  private String fullName;
  private String role;
  private String action;
}
