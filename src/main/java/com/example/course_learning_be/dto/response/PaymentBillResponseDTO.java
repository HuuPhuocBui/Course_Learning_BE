package com.example.course_learning_be.dto.response;

import com.example.course_learning_be.enums.PaymentStatus;
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
public class PaymentBillResponseDTO {
  private String merchantEmail;
  private PaymentStatus status;
  private String transactionId;
  private String cardType;
  private String orderInfo;
  private String amount;
  private String currency;
  private String bank;
  private String payDate;
}
