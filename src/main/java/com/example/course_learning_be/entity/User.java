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
@Document(collection = User.COLLECTION_NAME)
public class User {
   public static final String COLLECTION_NAME = "User";
   @Id
   private String userId;
   private String email;
   private String fullName;
   private String password;

   public User() {
      this.userId = UUID.randomUUID().toString();
   }
}
