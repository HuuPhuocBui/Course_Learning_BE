package com.example.course_learning_be.service;

import com.cloudinary.Api;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.Url;
import com.cloudinary.utils.ObjectUtils;
import com.example.course_learning_be.dto.request.VideoUploadRequestDTO;
import com.example.course_learning_be.dto.response.VideoResponseDTO;
import com.example.course_learning_be.dto.response.VideoUploadResponseDTO;
import com.example.course_learning_be.entity.Chunk;
import com.example.course_learning_be.entity.General;
import com.example.course_learning_be.entity.Lesson;
import com.example.course_learning_be.entity.Video;
import com.example.course_learning_be.entity.VideoUrlAndThumbnail;
import com.example.course_learning_be.repository.GeneralRepository;
import com.example.course_learning_be.repository.LessonRepository;
import com.example.course_learning_be.repository.VideoRepository;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j(topic = "VIDEO-SERVICE")
public class VideoService {

  private final VideoRepository videoRepository;
  private final LocalStorageService localStorageService;
  private final VideoFrameService videoFrameService;
  private final GeneralService generalService;
  private final GeneralRepository generalRepository;
  private final CloudinaryService cloudinaryService;
  private final LessonRepository lessonRepository;
  private final Cloudinary cloudinary;


  public VideoUploadResponseDTO initVideo(VideoUploadRequestDTO requestDTO) {
    log.info("Init video: {}", requestDTO.getTitle());
    Video video = Video.builder()
        .title(requestDTO.getTitle())
        .description(requestDTO.getDescription())
        .expectedTotalChunk(requestDTO.getTotalChunk())
        .build();

    videoRepository.save(video);

    return VideoUploadResponseDTO.builder()
        .videoId(video.getId())
        .totalChunk(video.getExpectedTotalChunk())
        .chunkCompleteness(false)
        .description(video.getDescription())
        .title(video.getTitle())
        .build();
  }

  public void deleteChunks(Video video) {
    log.info("Video chunk count {}", video.getChunkCount());
    if (video.getChunkCount() != 0) {
      List<String> list = video.getChunks().values().stream()
          .map(Chunk::getUrl)
          .toList();

      list.forEach(localStorageService::deleteFileFromRelativeUrl);
      video.clearChunks();
      videoRepository.save(video);
    }

  }

  public String getFullInfoVideoUrl(String videoId) throws IOException, NoSuchAlgorithmException {
    Video video = videoRepository.findById(videoId)
        .orElseThrow(() -> new RuntimeException("Course not found "));

    List<String> list = video.getChunks().values().stream()
        .map(Chunk::getUrl)
        .toList();

    VideoUrlAndThumbnail merged = this.mergeChunksAndUploadToCloudinary(list);
    video.setVideoUrl(merged.getVideoUrl());
    video.setPublicId(merged.getPublicId());
    video.setThumbnailUrl(merged.getThumbnailUrl());
    video.setDuration(merged.getDuration());
    video.clearChunks();
    videoRepository.save(video);
    return video.getPublicId();
  }

  public VideoUrlAndThumbnail mergeChunksAndUploadToCloudinary(List<String> chunkUrls)
      throws IOException {

    // 1️⃣ Merge chunks vào temp file (giữ nguyên)
    File mergedFile = File.createTempFile("merged_", ".mp4");
    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(mergedFile))) {
      byte[] buffer = new byte[8 * 1024];
      for (String url : chunkUrls) {
        try (InputStream in = url.startsWith("http") ? new URL(url).openStream()
            : new FileInputStream(url)) {
          int read;
          while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
          }
        }
      }
    }

    // 2️⃣ Upload video với type "private"
    Map uploadVideoResult = cloudinaryService.getCloudinary().uploader().uploadLarge(
        mergedFile,
        ObjectUtils.asMap(
            "resource_type", "video",
            "type", "private",
            "folder", "merged_videos",
            "chunk_size", 20 * 1024 * 1024
        )
    );

    String publicId = uploadVideoResult.get("public_id").toString();

    // 3️⃣ Tạo signed URL với TTL - CÁCH ĐÚNG
    String videoUrl = generateSignedUrl(publicId, "video", 150, "mp4", null);

    // 4️⃣ Tạo signed thumbnail URL với TTL
    String thumbnailUrl = generateSignedUrl(publicId, "video", 150, "jpg",
        new Transformation().startOffset("2.0"));

    String duration;
    try {
      duration = getVideoDuration(mergedFile.getAbsolutePath());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      duration = "unknown";
    }

    // 6️⃣ Xóa temp file
    mergedFile.delete();

    return VideoUrlAndThumbnail.builder()
        .videoUrl(videoUrl)
        .thumbnailUrl(thumbnailUrl)
        .duration(duration)
        .publicId(publicId) // 👈 LƯU PUBLIC_ID
        .build();
  }

  private String generateSignedUrl(String publicId, String resourceType,
      long ttlSeconds, String format,
      Transformation transformation) {
    Cloudinary cloudinary = cloudinaryService.getCloudinary();

    try {
      // Sử dụng ttlSeconds để tính expiration time
      long expirationTime = System.currentTimeMillis() / 1000 + ttlSeconds;

      // Tạo parameters cho signature - bao gồm expires_at
      Map<String, Object> paramsToSign = new HashMap<>();
      paramsToSign.put("expires_at", expirationTime);

      // Tạo signature với expiration
      String signature = cloudinary.apiSignRequest(paramsToSign, cloudinary.config.apiSecret);

      // Tạo URL base
      Url url = cloudinary.url()
          .resourceType(resourceType)
          .type("private");

      if (format != null) {
        url.format(format);
      }
      if (transformation != null) {
        url.transformation(transformation);
      }

      // Generate base URL (không signed)
      String baseUrl = url.generate(publicId);

      // Thêm signature và expiration vào URL theo định dạng Cloudinary
      // Định dạng: /s--SIGNATURE--/vEXPIRES/public_id
      String signedUrl = baseUrl.replace("/upload/",
          "/upload/s--" + signature.substring(0, 8) + "--/v" + expirationTime + "/");

      return signedUrl;

    } catch (Exception e) {
      throw new RuntimeException("Error generating signed URL with TTL", e);
    }
  }

  // Phương thức tạo signed URL với TTL
//  private String generateSignedUrl(String publicId, String resourceType,
//      long ttlSeconds, String format,
//      Transformation transformation) {
//    Cloudinary cloudinary = cloudinaryService.getCloudinary();
//
//    try {
//      // Tạo timestamp hiện tại
//      long timestamp = System.currentTimeMillis() / 1000;
//
//      // Tạo URL với signed=true
//      Url url = cloudinary.url()
//          .resourceType(resourceType)
//          .type("private")
//          .signed(true);
//
//      if (format != null) {
//        url.format(format);
//      }
//      if (transformation != null) {
//        url.transformation(transformation);
//      }
//
//      // Generate URL - Cloudinary sẽ tự động thêm timestamp và signature
//      String generatedUrl = url.generate(publicId);
//
//      return generatedUrl;
//
//    } catch (Exception e) {
//      throw new RuntimeException("Error generating signed URL", e);
//    }
//  }
//  public VideoUrlAndThumbnail mergeChunksAndUploadToCloudinary(List<String> chunkUrls)
//      throws IOException {
//
//    // 1️⃣ Merge chunks vào temp file (giữ nguyên)
//    File mergedFile = File.createTempFile("merged_", ".mp4");
//    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(mergedFile))) {
//      byte[] buffer = new byte[8 * 1024];
//      for (String url : chunkUrls) {
//        try (InputStream in = url.startsWith("http") ? new URL(url).openStream()
//            : new FileInputStream(url)) {
//          int read;
//          while ((read = in.read(buffer)) != -1) {
//            out.write(buffer, 0, read);
//          }
//        }
//      }
//    }
//
//    // 2️⃣ Upload video với type "private"
//    Map uploadVideoResult = cloudinaryService.getCloudinary().uploader().uploadLarge(
//        mergedFile,
//        ObjectUtils.asMap(
//            "resource_type", "video",
//            "type", "private", // 👈 THAY ĐỔI: chuyển sang private
//            "folder", "merged_videos",
//            "chunk_size", 20 * 1024 * 1024
//        )
//    );
//
//    String publicId = uploadVideoResult.get("public_id").toString();
//    long expirationTime = System.currentTimeMillis() / 1000 + 3600;
//
//    // 3️⃣ Tạo signed URL với TTL
//    String videoUrl = cloudinaryService.getCloudinary().url()
//        .resourceType("video")
//        .type("private")
//        .format("mp4")
//        .format("mp4")
//        .version(uploadVideoResult.get("version"))
//        .signed(true) // 👈 THÊM: ký URL
//        .generate(publicId + "?expires=" + expirationTime); // 👈 THÊM: TTL 1 giờ
//
//
//    // 4️⃣ Tạo signed thumbnail URL với TTL
//    String thumbnailUrl = cloudinaryService.getCloudinary().url()
//        .resourceType("video")
//        .type("private")
//        .version(uploadVideoResult.get("version")) // 👈 CẦN version
//        .signed(true) // 👈 signed=true
//        .transformation(new Transformation().startOffset(2.0).fetchFormat("jpg"))
//        .generate(publicId + "?expires=" + expirationTime);
//
//    String duration;
//    try {
//      duration = getVideoDuration(mergedFile.getAbsolutePath());
//    } catch (InterruptedException e) {
//      Thread.currentThread().interrupt();
//      duration = "unknown";
//    }
//
//    // 6️⃣ Xóa temp file
//    mergedFile.delete();
//
//    return VideoUrlAndThumbnail.builder()
//        .videoUrl(videoUrl)
//        .thumbnailUrl(thumbnailUrl)
//        .duration(duration)
//        .build();
//  }



//  public VideoUrlAndThumbnail mergeChunksAndUploadToCloudinary(List<String> chunkUrls)
//      throws IOException {
//    // 1️⃣ Merge chunks vào temp file
//    File mergedFile = File.createTempFile("merged_", ".mp4");
//    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(mergedFile))) {
//      byte[] buffer = new byte[8 * 1024];
//      for (String url : chunkUrls) {
//        try (InputStream in = url.startsWith("http") ? new URL(url).openStream()
//            : new FileInputStream(url)) {
//          int read;
//          while ((read = in.read(buffer)) != -1) {
//            out.write(buffer, 0, read);
//          }
//        }
//      }
//    }
//
//    // 2️⃣ Upload video bình thường
////    Map uploadVideoResult = cloudinaryService.getCloudinary().uploader().upload(
////        mergedFile,
////        ObjectUtils.asMap(
////            "resource_type", "video",
////            "folder", "merged_videos"
////        )
////    );
//    Map uploadVideoResult = cloudinaryService.getCloudinary().uploader().uploadLarge(
//        mergedFile,
//        ObjectUtils.asMap(
//            "resource_type", "video",
////            "type", "authenticated",
//            "folder", "merged_videos",
//            "chunk_size", 20 * 1024 * 1024 // 20MB, có thể điều chỉnh
//        )
//    );
//
//    String publicId = uploadVideoResult.get("public_id").toString();
//
//    // 3️⃣ Lấy HLS URL
//    String videoHlsUrl = cloudinaryService.getCloudinary().url()
//        .resourceType("video")
////        .type("authenticated")
//        .type("upload")
//        .format("mp4")
//        .generate(publicId);
//
//    // 4️⃣ Thumbnail từ video cloud (2s)
//    String thumbnailUrl = cloudinaryService.getCloudinary().url()
//        .resourceType("video")
////        .type("authenticated")
//        .transformation(new Transformation().startOffset(2.0).fetchFormat("jpg"))
//        .generate(publicId);
//
//    String duration;
//    try {
//      duration = getVideoDuration(mergedFile.getAbsolutePath());
//    } catch (InterruptedException e) {
//      Thread.currentThread().interrupt(); // TIÊU CHUẨN KHI CATCH InterruptedException
//      duration = "unknown";
//    }
//
//    // 6️⃣ Xóa temp file
//    mergedFile.delete();
//
//    return VideoUrlAndThumbnail.builder()
//        .videoUrl(videoHlsUrl)
//        .thumbnailUrl(thumbnailUrl)
//        .duration(duration)
//        .build();
//  }

  private String getVideoDuration(String filePath) throws InterruptedException, IOException {
    ProcessBuilder builder = new ProcessBuilder(
        "ffprobe",
        "-v", "error",
        "-show_entries", "format=duration",
        "-of", "default=noprint_wrappers=1:nokey=1",
        filePath
    );
    builder.redirectErrorStream(true);

    Process process = builder.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

    String line = reader.readLine();

    process.waitFor();

    if (line != null) {
      return line;

    }
    return "bug";

  }

  public VideoUploadResponseDTO initVideoHomepage(VideoUploadRequestDTO requestDTO) {
    Video video = Video.builder()
        .title(requestDTO.getTitle())
        .description(requestDTO.getDescription())
        .expectedTotalChunk(requestDTO.getTotalChunk())
        .build();

    videoRepository.save(video);

    General general = generalService.getSingletonGeneral();
    general.setVideoHomePageId(video.getId());
    generalRepository.save(general);

    return VideoUploadResponseDTO.builder()
        .videoId(video.getId())
        .totalChunk(video.getExpectedTotalChunk())
        .chunkCompleteness(false)
        .description(video.getDescription())
        .title(video.getTitle())
        .build();
  }

  public VideoResponseDTO getHomePageVideo() {
    String videoId = generalService.getHomepageVideoId();

    Video video = videoRepository.findById(videoId)
        .orElseThrow(() -> new RuntimeException("Video not found "));
    return VideoResponseDTO.builder()
        .totalChunk(video.getExpectedTotalChunk())
        .id(videoId)
        .videoUrl(video.getVideoUrl())
        .thumbnailUrl(video.getThumbnailUrl())
        .duration(video.getDuration())
        .totalSize(video.getSize())
        .build();

  }


}
