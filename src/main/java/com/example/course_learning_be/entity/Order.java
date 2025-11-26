package com.example.course_learning_be.entity;

import com.example.course_learning_be.enums.OrderStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
@Slf4j
@Document(collection = Order.COLLECTION_NAME)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class Order {
  public static final String COLLECTION_NAME = "Order";
  @Id
  private String id;
  @Field(name = "code")
  private String code;

  @Field(name = "user_id")
  private String userId;

  @Field(name = "course_ids")
  @Builder.Default
  private List<String> courseIds = new ArrayList<>();

  @Field(name = "status")
  @Builder.Default
  private OrderStatus status = OrderStatus.PENDING;

  @Field(name = "payment_method")
  private String paymentMethod;

  @Field(name = "raised")
  private LocalDate raisedAt;

  @Field(name = "created_at")
  private Instant createdAt;

  @Field(name = "paid_at")
  private LocalDate paidAt;

  @Field(name = "transaction_id")
  private String transactionId;

  @Field(name = "total_amount")
  private long totalAmount;


  public Order() {
    this.id = UUID.randomUUID().toString();
  }


}
