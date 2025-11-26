package com.example.course_learning_be.service;

import com.cloudinary.Cloudinary;
import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CloudinarySecurityService {
  private final Cloudinary cloudinary;
  @Value("${cloudinary.cloud-name}")
  private String cloudName;

  @Value("${cloudinary.api-key}")
  private String apiKey;

  @Value("${cloudinary.api-secret}")
  private String apiSecret;
  public String generateSignedUrl(String publicId, long expireSeconds) {
    try {
      long expiration = System.currentTimeMillis() / 1000 + expireSeconds;

      // Tạo chữ ký từ Cloudinary SDK
      Map<String, Object> params = new HashMap<>();
      params.put("public_id", publicId);
      params.put("timestamp", expiration);

      String signature = cloudinary.apiSignRequest(params, apiSecret);

      // Trả URL signed
      return String.format(
          "https://res.cloudinary.com/%s/video/authenticated/%s.mp4?_a=%s&_t=%d",
          cloudName,
          publicId,
          signature,
          expiration
      );
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate signed URL", e);
    }
  }
//public String generateSignedUrl(String publicId) {
//  return cloudinary.url()
//      .resourceType("video")
//      .type("authenticated")
//      .secure(true)
//      .signed(true)
//      .generate(publicId + ".mp4");
//}
//
//  // Tạo signed URL cho thumbnail authenticated
//  public String generateSignedThumbnailUrl(String publicId) {
//    return cloudinary.url()
//        .resourceType("image")
//        .type("authenticated")
//        .secure(true)
//        .signed(true)
//        .generate(publicId + ".jpg");
//  }
}
