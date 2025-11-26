package com.example.course_learning_be.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@Document(collection = CourseStudent.COLLECTION_NAME)
public class CourseStudent {

  public static final String COLLECTION_NAME = "courseStudents";

  @Id
  @Field(name = "course_id")
  private String courseId;

  @Field(name = "user_id")
  @Builder.Default
  private List<String> userIds = new ArrayList<>();

  public CourseStudent() {
    this.userIds = new ArrayList<>();
  }

  public void addStudent(String userId) {
    this.userIds.add(userId);
  }
}

