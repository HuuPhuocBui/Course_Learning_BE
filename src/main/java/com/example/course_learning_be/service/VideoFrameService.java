package com.example.course_learning_be.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j(topic = "VIDEO-FRAME-SERVICE")
public class VideoFrameService {

  public String extractFrameWithCrop(String videoPath, String outputDir) throws IOException, InterruptedException {
    // ✅ Tạo thư mục output nếu chưa có
    Files.createDirectories(Paths.get(outputDir));

    // ✅ Tạo tên file thumbnail duy nhất
    String outputFile = outputDir + File.separator + "frame_" + UUID.randomUUID() + ".jpg";
    String ffmpegPath = "D:\\ffmpeg-2025-11-10-git-133a0bcb13-essentials_build\\ffmpeg-2025-11-10-git-133a0bcb13-essentials_build\\bin\\ffmpeg.exe";
    // ✅ Lệnh ffmpeg
    List<String> command = List.of(
        ffmpegPath,
        "-ss", "1",
        "-i", videoPath,
        "-frames:v", "1",
        "-filter:v", "crop=iw/2:ih/2:(iw-iw/2)/2:(ih-ih/2)/2",
        "-y",
        outputFile
    );


    Process process;

    try {
      // ✅ Khởi tạo process
      ProcessBuilder pb = new ProcessBuilder(command);
      pb.redirectErrorStream(true);
      process = pb.start();
    } catch (IOException e) {
      log.error("❌ Failed to start ffmpeg process. Command: {}", String.join(" ", command), e);
      throw e; // ném ra để BE biết
    }

    // ✅ Đọc log của ffmpeg
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        log.info("[ffmpeg] {}", line);
      }
    }

    // ✅ Chờ ffmpeg chạy xong
    int exitCode = process.waitFor();
    if (exitCode != 0) {
      // Nếu exitCode != 0, log lỗi chi tiết
      log.error("❌ FFmpeg exited with code {}. Check command and paths.", exitCode);
      throw new IOException("FFmpeg failed with exit code " + exitCode);
    }

    // ✅ Trả về đường dẫn file thumbnail
    log.info("✅ Thumbnail generated successfully: {}", outputFile);
    return outputFile;
  }
}
