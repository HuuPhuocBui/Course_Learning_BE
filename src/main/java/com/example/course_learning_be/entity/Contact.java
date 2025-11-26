package com.example.course_learning_be.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@FieldNameConstants
@Builder
@Document(collection = Contact.COLLECTION_NAME)
public class Contact {
  public static final String COLLECTION_NAME = "Contact";
  @Id
  private String id;
  @Field(name = "fullName")
  private String fullName;
  @Field(name = "email")
  private String email;
  @Field(name = "phone")
  private String phoneNumber;
  @Field(name = "title")
  private String titleCourse;
  @Field(name = "message")
  private String message;
  public Contact() {
    this.id = UUID.randomUUID().toString();
  }
}
