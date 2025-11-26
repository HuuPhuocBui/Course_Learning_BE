package com.example.course_learning_be.dto.response;

import com.example.course_learning_be.enums.CourseAccessLevel;
import com.example.course_learning_be.enums.CourseLevel;
import com.example.course_learning_be.enums.Language;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResponseDTO {
  private String id;
  private String title;
  private String description;
  private String content;
  private String duration;
  private long price;
  private CourseLevel level;
  private CourseAccessLevel accessLevel;
  private Language language;
  private String ownerEmail;
  private String authorName;
  private String pinImageUrl;
  private List<String> previewImageUrls;

  private int totalLessons;

  private List<CourseResponseDTO.CurriculumDTO> curriculums;


  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class CurriculumDTO {
    private String id;
    public String title;
    public String description;
    public List<CourseResponseDTO.LessonDTO> lessons;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class LessonDTO {
    private String id;
    private String title;
    private String duration;

  }

}
