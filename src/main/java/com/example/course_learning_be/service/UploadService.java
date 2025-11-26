package com.example.course_learning_be.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.course_learning_be.entity.Course;
import com.example.course_learning_be.repository.MongoService;
import java.io.IOException;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class UploadService {
  private final Cloudinary cloudinary;
  private final MongoService mongoService;
  public String uploadImage(MultipartFile file, String courseId) throws IOException {
    Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
        ObjectUtils.asMap(
            "resource_type", "image"
        ));
    String imageUrl = uploadResult.get("url").toString();

    Course course = mongoService.findById(courseId, Course.class)
        .orElseThrow(() -> new RuntimeException("Course not found"));


    mongoService.save(course);

    return imageUrl;
  }

  public String deleteImage(String publicId) throws IOException {
    Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    return result.get("result").toString();
  }

  public Map<String, Object> uploadLargeVideo(MultipartFile file) throws IOException {
    // Dùng InputStream để tránh load toàn bộ file vào RAM
    try (var inputStream = file.getInputStream()) {
      return cloudinary.uploader().uploadLarge(
          inputStream,
          ObjectUtils.asMap(
              "resource_type", "video",
              "chunk_size", 6000000,
              "folder", "videos"
          ),
          null
      );
    }

  }
}
