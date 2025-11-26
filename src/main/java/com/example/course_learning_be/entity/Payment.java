package com.example.course_learning_be.entity;

import com.example.course_learning_be.enums.PaymentStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Slf4j
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@Document(collection = Payment.COLLECTION_NAME)
public class Payment {
  public static final String COLLECTION_NAME = "Payment";
  @Id
  private String paymentId;
  private String merchantId;
  private PaymentStatus status;
  private String orderId;
  private String transactionId;
  private String cardType;
  private String orderInfo;
  private String amount;
  private String currency;
  private String bank;
  private String payDate;
  public Payment() {
    this.paymentId = UUID.randomUUID().toString();
  }
}
