package com.example.course_learning_be.entity;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@FieldNameConstants
@Builder
@Document(collection = ExerciseFile.COLLECTION_NAME)
public class ExerciseFile {
  public static final String COLLECTION_NAME = "ExerciseFile";
  @Id
  private String id;

  private String publicId; // ID Cloudinary

  private String url; // URL Cloudinary

  private String fileName;

  private String fileType;
  public ExerciseFile() {
    this.id = UUID.randomUUID().toString();
  }
}
