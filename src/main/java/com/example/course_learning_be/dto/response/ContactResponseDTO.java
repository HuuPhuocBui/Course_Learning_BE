package com.example.course_learning_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactResponseDTO {
     private String fullName;
     private String email;
     private String course;
     private String comment;
     private String phone;
}
