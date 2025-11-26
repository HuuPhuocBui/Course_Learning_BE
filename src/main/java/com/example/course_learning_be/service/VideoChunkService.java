package com.example.course_learning_be.service;

import com.example.course_learning_be.entity.Chunk;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
@RequiredArgsConstructor
@Slf4j(topic = "VIDEO-CHUNK-SERVICE")
public class VideoChunkService {
  private final LocalStorageService localStorageService;
  public Chunk chunkBuilder(MultipartFile file, long chunkIndex) throws Exception {
    String fileUrl = localStorageService.upload(file);

    double chunkSize = this.calculateChunkSizeInBytes(fileUrl);

    return Chunk.builder()
        .url(fileUrl)
        .index(chunkIndex)
        .length("not important")
        .thumbnailUrl("thumbnailUrl")
        .chunkSize(chunkSize)
        .build();
  }
  public double calculateChunkSizeInBytes(String fileUrl) {
    try {
      Path chunkPath = Paths.get(fileUrl);
      if (Files.exists(chunkPath)) {
        return Files.size(chunkPath) / (1024.0 * 1024);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return -1;
  }
}
