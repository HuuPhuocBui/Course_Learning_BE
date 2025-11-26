package com.example.course_learning_be.dto.response;

import com.example.course_learning_be.enums.CourseAccessLevel;
import com.example.course_learning_be.enums.CourseLevel;
import com.example.course_learning_be.enums.Language;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseResponse {
  private String id;
  private String title;
  private String description;
  private String content;
  private String duration;

  // Dùng enum cho thống nhất với entity
  private CourseLevel level;
  private CourseAccessLevel accessLevel;
  private Language language;

  private List<String> previewImageUrls;

  // Giữ đúng tên pinImageUrl như trong entity
  private String pinImageUrl;

  private long price;
  private String ownerName;
}
