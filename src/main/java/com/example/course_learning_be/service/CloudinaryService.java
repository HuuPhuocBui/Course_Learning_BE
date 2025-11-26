package com.example.course_learning_be.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service

public class CloudinaryService{
  @Autowired
  private Cloudinary cloudinary;
  private final String apiSecret;
  private final String apiKey;
  private final String cloudName;

  public CloudinaryService(Cloudinary cloudinary,
      @Value("${cloudinary.api-secret}") String apiSecret,
      @Value("${cloudinary.cloud-name}") String cloudName,
      @Value("${cloudinary.api-key}") String apiKey) {
    this.cloudinary = cloudinary;
    this.apiSecret = apiSecret;
    this.cloudName = cloudName;
    this.apiKey = apiKey;
  }

  public String getApiSecret() {
    return apiSecret;
  }
  public String getCloudName() {
    return cloudName;
  }
  public String getApiKey() {
    return apiKey;
  }


  public String uploadFile(MultipartFile file) {
    try {
      Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of("folder", "courses"));
      return uploadResult.get("secure_url").toString();
    } catch (IOException e) {
      throw new RuntimeException("Upload file to Cloudinary failed", e);
    }
  }

  public List<String> uploadFiles(MultipartFile[] files) {
    List<String> urls = new ArrayList<>();
    for (MultipartFile file : files) {
      urls.add(uploadFile(file));
    }
    return urls;
  }
  public Map uploadFile(MultipartFile file, String folder) throws IOException {
    Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
        "folder", folder,
        "resource_type", "raw", // bắt buộc cho ZIP/PDF
        "public_id", file.getOriginalFilename() != null ? file.getOriginalFilename().replaceAll("\\s+", "_") : null
    ));
    return uploadResult;
  }
  public Cloudinary getCloudinary() {
    return cloudinary;
  }

}
