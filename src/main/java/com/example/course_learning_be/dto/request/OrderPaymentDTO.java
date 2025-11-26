package com.example.course_learning_be.dto.request;

import com.example.course_learning_be.enums.OrderStatus;
import java.time.Instant;
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
public class OrderPaymentDTO {
  private String orderId;
  private String orderCode;
  private String userId;
  private List<String> courseIds;
  private OrderStatus orderStatus;
  private long totalAmount;
  private Instant createdAt;
}
