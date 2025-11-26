package com.example.course_learning_be.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
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
@Document(collection = Curriculum.COLLECTION_NAME)
public class Curriculum {
  public static final String COLLECTION_NAME = "Curriculum";
  @Id
  private String id;
  @Field(name = "course_id")
  private String courseId;

  @Field(name = "title")
  private String title;

  @Field(name = "description")
  private String description;

  @Field(name = "position")
  private int position;

  @Field(name = "lessons")
  @Builder.Default
  private TreeMap<Integer, Lesson> lessons = new TreeMap<>();

  @Builder.Default
  private List<Integer> orderKeeper = new ArrayList<>();
  public Curriculum() {
    this.id = UUID.randomUUID().toString();
  }


  public void refreshOrder() {
    TreeMap<Integer, Lesson> updatedMap = new TreeMap<>();
    AtomicInteger counter = new AtomicInteger(100);

    for (Lesson lesson : this.lessons.values()) {
      int newPosition = counter.getAndAdd(100);
      lesson.setPosition(newPosition); // update internal field
      updatedMap.put(newPosition, lesson); // use it as key
    }

    this.lessons = updatedMap;
  }

  public void refreshKeeper() {
    this.orderKeeper = new ArrayList<>(this.lessons.keySet());
  }

  public void removeLesson(int actualPos) {
    this.lessons.remove(actualPos);
    refreshOrder();
    refreshKeeper();

  }
}
