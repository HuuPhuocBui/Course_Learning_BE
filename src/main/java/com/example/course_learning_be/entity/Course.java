package com.example.course_learning_be.entity;

import com.example.course_learning_be.enums.CourseAccessLevel;
import com.example.course_learning_be.enums.CourseLevel;
import com.example.course_learning_be.enums.Language;
import java.util.ArrayList;
import java.util.List;
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
@Document(collection = Course.COLLECTION_NAME)
public class Course {
  public static final String COLLECTION_NAME = "Course";

  @Id
  private String id;
  @Field(name = "title")
  private String title;

  @Field(name = "description")
  private String description;

  @Field(name = "content")
  private String content;

  @Field(name = "duration")
  private String duration;

  @Field(name = "level")
  private CourseLevel level;

  @Field(name = "access_level")
  private CourseAccessLevel accessLevel;

  @Field
  @Builder.Default
  private Language language = Language.EN;

  @Field(name = "preview_images")
  @Builder.Default
  private List<String> previewImageUrls = new ArrayList<>();

  @Field(name = "owner")
  private User owner;

  @Field(name = "pinned_img_url")
  @Builder.Default
  private String pinImageUrl = "/uploads/image-1.jpg";

  @Field(name = "price")
  @Builder.Default
  private long price = 0;
  @Field(name = "curriculums")
  @Builder.Default
  private List<Curriculum> curriculums = new ArrayList<>();
  @Builder.Default

  private List<Integer> orderKeeper = new ArrayList<>();
  public Course() {
    this.id = UUID.randomUUID().toString();
  }
  // trong class Course.java
  public void addByPosSmartWay(Curriculum newCurriculum, int right) {
    if (curriculums == null) {
      curriculums = new ArrayList<>();
    }

    // Nếu position < 0 thì ép về 0
    int insertIndex = Math.max(0, right);

    // Nếu position lớn hơn size hiện tại -> thêm vào cuối
    if (insertIndex >= curriculums.size()) {
      curriculums.add(newCurriculum);
    } else {
      curriculums.add(insertIndex, newCurriculum);
    }

    // Cập nhật lại position cho toàn bộ curriculum trong course
    for (int i = 0; i < curriculums.size(); i++) {
      curriculums.get(i).setPosition(i + 1);
    }
  }


}
