package com.example.course_learning_be.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
@Document(collection = User.COLLECTION_NAME)
public class User {
   public static final String COLLECTION_NAME = "User";
   @Id
   private String id;
   private String email;
   private String fullName;
   private String password;
   private String avatar;
   private String role;
   private String action;
   private List<String> purchasedCourseIds;
   @Field(name = "avatar_image_url")
   @Builder.Default
   private String avatarUrl = "uploads/image-1.jpg";
   @Field
   @Builder.Default
   private Set<String> courses = new HashSet<>();

   public User() {
      this.id = UUID.randomUUID().toString();
   }
}
