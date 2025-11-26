package com.example.course_learning_be.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@SuperBuilder
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class BaseDocument {
  @Id
  @Field(name = "_id")
  private String id;

  @CreatedDate
  @Field(name = "created_at")
  private Instant createdAt;

  @LastModifiedDate
  @Field(name = "updated_at")
  private Instant updatedAt;

  @Field(name = "deleted_at")
  private LocalDateTime deletedAt;

  protected abstract String idGenerator();

  public abstract String getMongoField(String javaFieldName);

  // Utility method
  public void ensureId() {
    if (this.id == null || this.id.isBlank()) {
      this.id = idGenerator();
    }
  }

  public void clearAudit() {
    this.createdAt = null;
    this.updatedAt = null;
    this.deletedAt = null;

  }
}
