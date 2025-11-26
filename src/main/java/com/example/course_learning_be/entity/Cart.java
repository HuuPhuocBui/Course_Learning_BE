package com.example.course_learning_be.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Slf4j
@Document(collection = Cart.COLLECTION_NAME)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class Cart {
  public static final String COLLECTION_NAME = "Cart";
  @Id
  private String id;

  @CreatedDate
  @Field(name = "created_at")
  private Instant createdAt;

  @LastModifiedDate
  @Field(name = "updated_at")
  private Instant updatedAt;

  @Field(name = "deleted_at")
  private LocalDateTime deletedAt;

  // ===============================
  //          CART FIELDS
  // ===============================
  @Field(name = "user_id")
  private String userId;

  @Field(name = "cart_items")
  @Builder.Default
  private Set<String> cartItems = new HashSet<>();


  // ===============================
  //         CART METHODS
  // ===============================
  public Cart() {
    this.id = UUID.randomUUID().toString();
  }

  public void ensureId() {
    if (this.id == null || this.id.isBlank()) {
      this.id = "cart-" + UUID.randomUUID();
    }
  }

  public void addItemToCart(String courseId) {
    cartItems.add(courseId);
  }

  public void removeItemFromCart(String courseId) {
    cartItems.remove(courseId);
  }

  public void cleanCart() {
    cartItems.clear();
  }

  public void clearAudit() {
    this.createdAt = null;
    this.updatedAt = null;
    this.deletedAt = null;
  }
}
