package com.example.course_learning_be.entity;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@FieldNameConstants
@Document(collection = Course.COLLECTION_NAME)
public class Course {
  public static final String COLLECTION_NAME = "Course";
  @Id
  private String courseId;
  private String title;
  private String description;
  private String duration;
  private String level;
  private String authorName;
  private String price;
  private String imageUrl;

  public Course() {
    this.courseId = UUID.randomUUID().toString();
  }
}
