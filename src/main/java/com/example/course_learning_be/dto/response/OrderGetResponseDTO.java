package com.example.course_learning_be.dto.response;

import com.example.course_learning_be.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderGetResponseDTO {
  private String id;
  private String buyerEmail;
  private List<String> courseNames;
  private OrderStatus status;
  private String paymentMethod;

  private LocalDate paidAt;

  private long totalAmount;
}
