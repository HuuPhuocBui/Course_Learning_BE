package com.example.course_learning_be.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
@Component
@RequiredArgsConstructor
public class FileClient {
  private final Cloudinary cloudinary;

  public String uploadImageFile(MultipartFile file) {
    if (file == null || file.isEmpty()) return null;

    try {
      Map uploadResult = cloudinary.uploader().upload(
          file.getBytes(),
          ObjectUtils.asMap(
              "resource_type", "image",
              "folder", "user_avatar"
          )
      );

      return uploadResult.get("secure_url").toString();

    } catch (IOException e) {
      throw new RuntimeException("Upload image thất bại", e);
    }
  }
}
