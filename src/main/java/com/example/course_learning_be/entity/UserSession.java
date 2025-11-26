package com.example.course_learning_be.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@FieldNameConstants
@Document(collection = UserSession.COLLECTION_NAME)
public class UserSession {
  public static final String COLLECTION_NAME = "UserSession";
  @Id
  private String id;

  private String userId;       // Firebase UID
  private String deviceId;     // Thiết bị hiện tại
  private String token;        // BE token
  private boolean valid;       // session còn hợp lệ
  private LocalDateTime createdAt;
  public UserSession() {
    this.id = UUID.randomUUID().toString();
  }
}
