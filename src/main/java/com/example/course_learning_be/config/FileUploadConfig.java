package com.example.course_learning_be.config;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;
@Configuration
public class FileUploadConfig {
  @Bean
  public MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();

    // Tăng giới hạn kích thước
    factory.setMaxFileSize(DataSize.ofGigabytes(5));     // 5GB
    factory.setMaxRequestSize(DataSize.ofGigabytes(5));  // 5GB

    // Tùy chọn: ghi tạm file ra disk để tránh tràn RAM
    factory.setLocation(System.getProperty("java.io.tmpdir"));

    return factory.createMultipartConfig();
  }
}
