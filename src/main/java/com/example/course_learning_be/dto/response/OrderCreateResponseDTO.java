package com.example.course_learning_be.dto.response;

import com.example.course_learning_be.enums.OrderStatus;
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
public class OrderCreateResponseDTO {
  private String orderId;
  private String customerName;
  private String customerEmail;
  private int itemCount;
  private long totalPrice;
  private OrderStatus status;
  private String paymentUrl;
}
