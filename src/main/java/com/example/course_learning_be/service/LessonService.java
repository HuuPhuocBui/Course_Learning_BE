package com.example.course_learning_be.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.Url;
import com.cloudinary.utils.ObjectUtils;
import com.example.course_learning_be.Util.SecurityUtil;
import com.example.course_learning_be.dto.request.LessonRequestDTO;
import com.example.course_learning_be.dto.response.LessonLearningResponseDTO;
import com.example.course_learning_be.dto.response.LessonResponseDTO;
import com.example.course_learning_be.entity.Curriculum;
import com.example.course_learning_be.entity.ExerciseFile;
import com.example.course_learning_be.entity.Lesson;
import com.example.course_learning_be.entity.User;
import com.example.course_learning_be.entity.Video;
import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import com.example.course_learning_be.mapper.LessonMapper;
import com.example.course_learning_be.repository.CurriculumRepository;
import com.example.course_learning_be.repository.ExerciseFileRepository;
import com.example.course_learning_be.repository.LessonRepository;
import com.example.course_learning_be.repository.VideoRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LessonService {

  private final CurriculumRepository curriculumRepository;
  private final VideoRepository videoRepository;
  private final LessonRepository lessonRepository;
  private final LessonMapper lessonMapper;
  private final CurriculumService curriculumService;
  private final SecurityUtil securityUtil;
  private final VideoSecurityService videoSecurityService;
  private final Cloudinary cloudinary;
  private final ExerciseFileRepository exerciseFileRepository;
  private final CloudinaryService cloudinaryService;


  @Transactional
  public LessonResponseDTO createSimple(LessonRequestDTO requestDTO, String curriculumID) {
    Video video = new Video();
    Curriculum curriculum = curriculumRepository.findById(curriculumID)
        .orElseThrow(() -> new RuntimeException("Course not found "));
    if (requestDTO.getVideoId().equals("vd1")) {
      video.setVideoUrl("on testing");
    } else {
      video = videoRepository.findById(requestDTO.getVideoId())
          .orElseThrow(() -> new RuntimeException("Course not found "));
    }

    Lesson lesson = lessonMapper.fromRequestDTOToEntity(requestDTO, curriculum, video);
    saveToCurriculum(lesson, curriculum);

    return lessonMapper.fromEntityToResponseDTO(lesson);
  }

  public String handleZipAndUpload(MultipartFile zipFile) {
    Path tmpDir = null;
    try {
// 1️⃣ Tạo thư mục tạm
      tmpDir = Files.createTempDirectory("exercise_");

      // 2️⃣ Lưu file zip vào tạm
      Path zipPath = tmpDir.resolve(zipFile.getOriginalFilename());
      Files.copy(zipFile.getInputStream(), zipPath, StandardCopyOption.REPLACE_EXISTING);

      // 3️⃣ Giải nén zip
      unzip(zipPath.toFile(), tmpDir.toFile());

      // 4️⃣ Tìm file .docx trong thư mục tạm
      File docx = findDocxFile(tmpDir.toFile());
      if (docx == null) {
        throw new RuntimeException("Không có file .docx trong zip");
      }

      // 5️⃣ Upload file .docx lên Cloudinary
      Map uploadResult = cloudinary.uploader().upload(
          docx,
          ObjectUtils.asMap(
              "resource_type", "raw",
              "folder", "exercise_files"
          )
      );

      String publicId = (String) uploadResult.get("public_id");
      String secureUrl = (String) uploadResult.get("secure_url");

      // 6️⃣ Lưu thông tin vào DB
      ExerciseFile ef = new ExerciseFile();
      ef.setPublicId(publicId);
      ef.setUrl(secureUrl);
      exerciseFileRepository.save(ef);

      // 7️⃣ Trả ID của exercise file để frontend lưu vào lesson
      return ef.getId();

    } catch (Exception e) {
      throw new RuntimeException("Upload exercise file thất bại", e);
    } finally {
      // 8️⃣ Xoá thư mục tạm sau khi xử lý
      if (tmpDir != null && Files.exists(tmpDir)) {
        try {
          FileUtils.deleteDirectory(tmpDir.toFile());
        } catch (IOException ignored) {
        }
      }
    }

  }

  private void unzip(File zipFile, File destDir) throws IOException {
    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        File newFile = new File(destDir, entry.getName());
        if (entry.isDirectory()) {
          newFile.mkdirs();
        } else {
          new File(newFile.getParent()).mkdirs();
          try (FileOutputStream fos = new FileOutputStream(newFile)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = zis.read(buffer)) > 0) {
              fos.write(buffer, 0, len);
            }
          }
        }
      }
    }
  }

  private File findDocxFile(File dir) {
    File[] files = dir.listFiles();
    if (files == null) {
      return null;
    }

    for (File file : files) {
      if (file.isDirectory()) {
        File result = findDocxFile(file);
        if (result != null) {
          return result;
        }
      } else if (file.getName().toLowerCase().endsWith(".docx")) {
        return file;
      }
    }
    return null;

  }


  @Transactional
  protected void saveToCurriculum(Lesson lesson, Curriculum curriculum) {
    int right = 0;
    if (!(lesson.getPosition() == 1)) {
      right = lesson.getPosition() - 1;
    }
    lessonRepository.save(lesson);
    curriculumService.addLessonByPosSmartWay(curriculum, filterData(lesson), right);

    curriculumRepository.save(curriculum);
  }

  private Lesson filterData(Lesson lesson) {
    return Lesson.builder()
        .id(lesson.getId())
        .position(lesson.getPosition())
        .build();
  }

  public List<LessonResponseDTO> getAllInCurriculum(String curriculumId) {

    Curriculum curriculum = curriculumRepository.findById(curriculumId)
        .orElseThrow(() -> new RuntimeException("Course not found "));
    AtomicInteger index = new AtomicInteger(1);

    //get ordered curriculums from the TreeMap
    List<Lesson> orderedLessons = new ArrayList<>(curriculum.getLessons().values());

    //map curriculum ID to fetched Curriculum
    List<String> ids = orderedLessons.stream().map(Lesson::getId).toList();
    Map<String, Lesson> fetched = lessonRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(Lesson::getId, Function.identity()));

    //rebuild ordered list using original order
    return orderedLessons.stream()
        .map(c -> fetched.get(c.getId()))
        .map(c -> lessonMapper.fromEntityToResponseDTOWithPosition(c, index.getAndIncrement()))
        .toList();
  }

  public LessonResponseDTO update(LessonRequestDTO requestDTO, String lessonId) {
    Lesson lesson = lessonRepository.findById(lessonId)
        .orElseThrow(() -> new RuntimeException("Course not found "));
    lessonMapper.updateEntityFromRequestDTO(lesson, requestDTO);

    return lessonMapper.fromEntityToResponseDTO(lesson);
  }

  public Lesson getById(String id) {
    return lessonRepository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
  }

//  public LessonLearningResponseDTO getLessonLearning(String courseId, String lessonId) {
//    isHavingAccessToCourse(courseId);
//    Lesson lesson = lessonRepository.findById(lessonId)
//        .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
//    Video video = videoRepository.findById(lesson.getVideoId())
//        .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
//
//    String videoUrlWithTTL = generateSignedUrl(video.getPublicId(), "video", 150, "mp4", new Transformation().quality("auto"));
//
//    return LessonLearningResponseDTO.builder()
//        .title(lesson.getTitle())
//        .lessonId(lesson.getId())
//        .position(lesson.getPosition())
//        .video(LessonLearningResponseDTO.VideoResponseDTO.builder()
//            .thumbnailUrl(video.getThumbnailUrl())
//            .videoUrl(videoUrlWithTTL)
//            .duration(video.getDuration())
//            .id(video.getId())
//            .build())
//        .build();
//  }
public LessonLearningResponseDTO getLessonLearning(String courseId, String lessonId) {
  isHavingAccessToCourse(courseId);
  Lesson lesson = lessonRepository.findById(lessonId)
      .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
  Video video = videoRepository.findById(lesson.getVideoId())
      .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));

  // 👇 TÍNH TTL THEO DURATION VIDEO
  long ttlSeconds = calculateTTLFromDuration(video.getDuration());

  String videoUrlWithTTL = generateSignedUrl(video.getPublicId(), "video", ttlSeconds, "mp4",
      new Transformation().quality("auto"));

  System.out.println("🎯 Video duration: " + video.getDuration() + "s → TTL: " + ttlSeconds + "s");

  return LessonLearningResponseDTO.builder()
      .title(lesson.getTitle())
      .lessonId(lesson.getId())
      .position(lesson.getPosition())
      .video(LessonLearningResponseDTO.VideoResponseDTO.builder()
          .thumbnailUrl(video.getThumbnailUrl())
          .videoUrl(videoUrlWithTTL)
          .duration(video.getDuration())
          .id(video.getId())
          .build())
      .build();
}

  private long calculateTTLFromDuration(String duration) {
    try {
      // Parse duration từ string (format: "17.233333")
      double videoDuration = Double.parseDouble(duration);

      // 👇 CÔNG THỨC TTL THÔNG MINH
      long ttl;
      if (videoDuration <= 300) { // ≤ 5 phút
        ttl = (long) (videoDuration * 2); // TTL gấp đôi duration
      } else if (videoDuration <= 1800) { // ≤ 30 phút
        ttl = (long) (videoDuration * 1.5); // TTL gấp rưỡi duration
      } else { // > 30 phút
        ttl = (long) (videoDuration + 3600); // Duration + 1 giờ buffer
      }

      // Đảm bảo TTL tối thiểu 300s (5 phút) và tối đa 86400s (24 giờ)
      return Math.max(300, Math.min(ttl, 86400));

    } catch (Exception e) {
      System.out.println("⚠️ Cannot parse duration: " + duration + ", using default TTL");
      return 7200; // Fallback: 2 giờ
    }
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
          .type("private");  // 👈 DÙNG "private"

      if (format != null) {
        url.format(format);
      }
      if (transformation != null) {
        url.transformation(transformation);
      }

      // Generate base URL (không signed)
      String baseUrl = url.generate(publicId);

      // 👇 THAY THẾ "/private/" THAY VÌ "/upload/"
      String signedUrl = baseUrl.replace("/private/",
          "/private/s--" + signature.substring(0, 8) + "--/v" + expirationTime + "/");

      System.out.println("🔗 Generated URL: " + signedUrl);
      return signedUrl;

    } catch (Exception e) {
      throw new RuntimeException("Error generating signed URL with TTL", e);
    }
  }
//  private String generateSignedUrl(String publicId, String resourceType,
//      long ttlSeconds, String format,
//      Transformation transformation) {
//    Cloudinary cloudinary = cloudinaryService.getCloudinary();
//
//    try {
//      // Sử dụng ttlSeconds để tính expiration time
//      long expirationTime = System.currentTimeMillis() / 1000 + ttlSeconds;
//
//      // Tạo parameters cho signature - bao gồm expires_at
//      Map<String, Object> paramsToSign = new HashMap<>();
//      paramsToSign.put("expires_at", expirationTime);
//
//      // Tạo signature với expiration
//      String signature = cloudinary.apiSignRequest(paramsToSign, cloudinary.config.apiSecret);
//
//      // Tạo URL base
//      Url url = cloudinary.url()
//          .resourceType(resourceType)
//          .type("private");
//
//      if (format != null) {
//        url.format(format);
//      }
//      if (transformation != null) {
//        url.transformation(transformation);
//      }
//
//      // Generate base URL (không signed)
//      String baseUrl = url.generate(publicId);
//
//      // Thêm signature và expiration vào URL theo định dạng Cloudinary
//      // Định dạng: /s--SIGNATURE--/vEXPIRES/public_id
//      String signedUrl = baseUrl.replace("/upload/",
//          "/upload/s--" + signature.substring(0, 8) + "--/v" + expirationTime + "/");
//
//      return signedUrl;
//
//    } catch (Exception e) {
//      throw new RuntimeException("Error generating signed URL with TTL", e);
//    }
//  }

  //public LessonLearningResponseDTO getLessonLearning(String courseId, String lessonId) {
//  isHavingAccessToCourse(courseId); // Thêm userId để kiểm tra
//  User user = securityUtil.getCurrentUser();
//
//  Lesson lesson = lessonRepository.findById(lessonId)
//      .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
//
//  Video video = videoRepository.findById(lesson.getVideoId())
//      .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
//
//  // Lấy secure URLs
//  String secureVideoUrl = videoSecurityService.getSecureVideoUrl(video.getId(), user.getId());
//
//  return LessonLearningResponseDTO.builder()
//      .title(lesson.getTitle())
//      .lessonId(lesson.getId())
//      .position(lesson.getPosition())
//      .video(LessonLearningResponseDTO.VideoResponseDTO.builder()
//          .thumbnailUrl(video.getThumbnailUrl())
//          .videoUrl(secureVideoUrl)
//          .duration(video.getDuration())
//          .id(video.getId())
//          .build())
//      .build();
//}
  public void isHavingAccessToCourse(String courseId) {
    User user = securityUtil.getCurrentUser();
    if (user.getEmail().equals("admin")) {
      return;
    }
    if (!user.getCourses().contains(courseId)) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }
  }

//  public String generateSignedVideoUrl(String publicId, String userId, int expireSeconds) {
//    long timestamp = System.currentTimeMillis() / 1000 + expireSeconds;
//    String stringToSign = "public_id=" + publicId + "&user_id=" + userId + "&expire=" + timestamp;
//
//    String signature;
//    try {
//      Mac mac = Mac.getInstance("HmacSHA1");
//      mac.init(new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
//      signature = Hex.encodeHexString(mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8)));
//    } catch (Exception e) {
//      throw new RuntimeException("Không thể tạo signature", e);
//    }
//
//    return cloudinary.url()
//        .resourceType("video")
//        .type("authenticated")
//        .format("mp4")
//        .generate(publicId)
//        + "?user_id=" + userId
//        + "&expire=" + timestamp
//        + "&signature=" + signature;
//  }


  public ExerciseFile uploadExerciseFile(MultipartFile file) throws IOException {
    Map result = cloudinaryService.uploadFile(file, "exercises");

    ExerciseFile exerciseFile = new ExerciseFile();
    exerciseFile.setPublicId((String) result.get("public_id"));
    exerciseFile.setUrl((String) result.get("secure_url"));
    exerciseFile.setFileName(file.getOriginalFilename());
    exerciseFile.setFileType(file.getContentType());

    return exerciseFileRepository.save(exerciseFile);
  }

}
