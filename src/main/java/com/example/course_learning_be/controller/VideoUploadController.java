package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.request.VideoUploadRequestDTO;
import com.example.course_learning_be.dto.response.ChunkUploadResponseDTO;
import com.example.course_learning_be.dto.response.VideoResponseDTO;
import com.example.course_learning_be.dto.response.VideoUploadResponseDTO;
import com.example.course_learning_be.service.VideoService;
import com.example.course_learning_be.service.VideoUploadService;
import java.security.NoSuchAlgorithmException;
import org.springframework.core.io.Resource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file/video")
@Validated
@Slf4j(topic = "VIDEO-UPLOAD-CONTROLLER")
@RequiredArgsConstructor
public class VideoUploadController {
  private final VideoService videoService;
  private final VideoUploadService videoUploadService;
  private final String VIDEO_BASE_PATH = "D:/course_learning_be/uploads/merge/";

  @PostMapping("/init")
  public ResponseEntity<VideoUploadResponseDTO> initVideo(@RequestBody VideoUploadRequestDTO requestDTO) {
    log.info("Received video init request: {}", requestDTO);
    VideoUploadResponseDTO res = videoService.initVideo(requestDTO);
    return ResponseEntity.ok().body(
        res
    );
  }
  @PostMapping("/chunk/upload")
  public ResponseEntity<ChunkUploadResponseDTO> uploadChunk(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "videoId", required = true) String videoId,
      @RequestParam("chunkIndex") long chunkIndex) {
    ChunkUploadResponseDTO res = videoUploadService.uploadChunkWithTimeConstraint(file, videoId, chunkIndex);

    return ResponseEntity.ok().body(res);
  }

  @PostMapping("/{videoId}/chunk/merge")
  public ResponseEntity<?> mergeChunk(@PathVariable String videoId) throws IOException, NoSuchAlgorithmException {
    var url = videoService.getFullInfoVideoUrl(videoId);
    Map<String, String> res = Map.of("url", url);

    return ResponseEntity.ok().body(res);
  }
  @PostMapping("/homepage/init")
  public ResponseEntity<?> initVideoHomepage(@RequestBody VideoUploadRequestDTO requestDTO) {
    VideoUploadResponseDTO res = videoService.initVideoHomepage(requestDTO);
    return ResponseEntity.ok().body(res);
  }

  @GetMapping("/homepage")
  public ResponseEntity<VideoResponseDTO> getHomepageVideo() {
    VideoResponseDTO res = videoService.getHomePageVideo();

    return ResponseEntity.ok().body(res);

  }

  @GetMapping("/uploads/merge/{filename:.+}")
  public ResponseEntity<Resource> getVideo(@PathVariable String filename) throws MalformedURLException {
    File file = new File(VIDEO_BASE_PATH + filename);
    if (!file.exists()) {
      return ResponseEntity.notFound().build();
    }

    UrlResource resource = new UrlResource(file.toURI());

    // Tự detect content type theo extension
    String contentType = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;
    if (filename.endsWith(".m3u8")) contentType = "application/vnd.apple.mpegurl";
    if (filename.endsWith(".ts")) contentType = "video/MP2T";

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .body(resource);
  }



}
