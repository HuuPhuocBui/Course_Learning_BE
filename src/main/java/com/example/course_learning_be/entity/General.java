package com.example.course_learning_be.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
@Getter
@Setter
@Document("General")
public class General {
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

  @Field(name = "video_homepage_id")
  private String videoHomePageId;

  /**
   * Abstract method for subclasses to define their ID generation strategy.
   */
  protected String idGenerator() {
    return UUID.randomUUID().toString(); // hoặc WebConstant.GENERAL_SINGLETON_ID nếu singleton
  }

  /**
   * Ensure the entity has an ID before saving.
   */
  public void ensureId() {
    if (this.id == null || this.id.isBlank()) {
      this.id = idGenerator();
    }
  }

  /**
   * Clear audit fields.
   */
  public void clearAudit() {
    this.createdAt = null;
    this.updatedAt = null;
    this.deletedAt = null;
  }
}
