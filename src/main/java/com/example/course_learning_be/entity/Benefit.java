package com.example.course_learning_be.entity;

import com.example.course_learning_be.enums.Language;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Document(collection = Benefit.COLLECTION_NAME)
public class Benefit {
  public static final String COLLECTION_NAME = "benefits";
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
  @Field(name = "title")
  private Map<Language, String> title;

  @Field(name = "description")
  private Map<Language, String> description;

  public Benefit() {
    this.id = UUID.randomUUID().toString();
  }


}
