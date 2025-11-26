package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.response.ApiResponse;
import com.example.course_learning_be.service.UploadService;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
public class UploadController {
  private final UploadService uploadService;
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/image/{courseId}")
  public ApiResponse<String> uploadImage(
      @PathVariable String courseId,
      @RequestParam("file") MultipartFile file
      ) {
    try {
      String imageUrl = uploadService.uploadImage(file, courseId);
      return ApiResponse.<String>builder()
          .message("Upload successful")
          .data(imageUrl)
          .build();
    } catch (Exception e) {
      return ApiResponse.<String>builder()
          .message("Upload failed: " + e.getMessage())
          .data(null)
          .build();
    }
  }
  @PreAuthorize("hasRole('USER')")
  @DeleteMapping("/delete/image/{publicId}")
  public ApiResponse<String> deleteImage(@PathVariable String publicId) {
    try {
      String result = uploadService.deleteImage(publicId);
      return new ApiResponse<>("Image deleted successfully", result);
    } catch (Exception e) {
      return new ApiResponse<>("Delete failed: " + e.getMessage(), null);
    }
  }

  //upload video
  @PostMapping("/video")
  public ApiResponse<Map<String, Object>> uploadVideo(@RequestParam("file") MultipartFile file) {
    try {
      Map<String, Object> uploadResult = uploadService.uploadLargeVideo(file);
      return ApiResponse.<Map<String, Object>>builder()
          .message("Upload successful")
          .data(uploadResult)
          .build();
    } catch (IOException e) {
      e.printStackTrace();
      return ApiResponse.<Map<String, Object>>builder()
          .message("Upload failed: " + e.getMessage())
          .data(null)
          .build();
    }
  }

}
