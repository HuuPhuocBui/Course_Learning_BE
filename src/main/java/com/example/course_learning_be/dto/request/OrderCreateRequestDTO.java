package com.example.course_learning_be.dto.request;

import java.util.List;
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
public class OrderCreateRequestDTO {
  private List<String> courses;

  private long totalAmount;


  public OrderCreateRequestDTO(List<String> courseIds) {
    this.courses = courseIds;
  }
}
