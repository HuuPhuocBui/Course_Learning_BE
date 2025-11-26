package com.example.course_learning_be.entity;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Builder
@FieldNameConstants
@Document(collection = Purchase.COLLECTION_NAME)
public class Purchase {
  public static final String COLLECTION_NAME = "Purchase";
  @Id
  private String purchaseId;
  private String courseId;
  private String courseName;

  private String sellerId;

  private String buyerId;
  private String buyerName;
  private String buyerEmail;

  private long profit;

  private String buyDate;
  public Purchase() {
    this.purchaseId = UUID.randomUUID().toString();
  }
}
