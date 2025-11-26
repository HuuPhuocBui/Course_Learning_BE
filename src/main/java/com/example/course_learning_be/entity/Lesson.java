package com.example.course_learning_be.entity;

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
@Document(collection = Lesson.COLLECTION_NAME)
public class Lesson {
  public static final String COLLECTION_NAME = "Lesson";
  @Id
  private String id;
  @Field(name = "curriculum_id")
  private String curriculumId;

  @Field(name = "title")
  private String title;

  @Field(name = "video")
  private String videoId;

  @Field(name = "duration")
  private String duration;

  @Field(name = "position")
  private int position;
  public Lesson() {
    this.id = UUID.randomUUID().toString();
  }
}
