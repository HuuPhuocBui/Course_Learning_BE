package com.example.course_learning_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDTO {
  private String newEmail;

  private MultipartFile newAvatarFile;
  private String newAvatarUrl;
  private String newFullName;
}
