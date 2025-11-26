package com.example.course_learning_be.service;

import com.example.course_learning_be.entity.Chunk;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("localStorageService")
@RequiredArgsConstructor
@Slf4j
public class LocalStorageService {
  public boolean deleteFileFromRelativeUrl(String relativeUrl) {
    // Ensure it's relative to the project root
    String baseDir = System.getProperty("user.dir");

    // Normalize relative URL to a file path
    if (relativeUrl.startsWith("/")) {
      relativeUrl = relativeUrl.substring(1);
    }

    File file = new File(baseDir, relativeUrl);

    if (file.exists()) {
      boolean deleted = file.delete();
      if (!deleted) {
        log.info("Failed to delete: " + file.getAbsolutePath());
      }
      return deleted;
    } else {
      log.info("File not found: " + file.getAbsolutePath());
      return false;
    }
  }

  public String upload(MultipartFile file) throws IOException {

    String fileName = file.getOriginalFilename();
    String uploadDir = new File("uploads").getAbsolutePath();
    Path uploadPath = Paths.get(uploadDir);

    Files.createDirectories(uploadPath);

    String unique = String.valueOf(UUID.randomUUID());

    Path filePath = uploadPath.resolve(unique + fileName);
    file.transferTo(filePath.toFile());

    return "uploads/" + unique + fileName;

  }

}
