package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.request.CourseRequestDTO;
import com.example.course_learning_be.dto.request.CurriculumRequestDTO;
import com.example.course_learning_be.dto.response.ApiResponse;
import com.example.course_learning_be.dto.response.CourseResponse;
import com.example.course_learning_be.dto.response.CourseResponseDTO;
import com.example.course_learning_be.dto.response.CurriculumResponseDTO;
import com.example.course_learning_be.dto.response.ExerciseFileResponse;
import com.example.course_learning_be.dto.response.PageResponse;
import com.example.course_learning_be.entity.ExerciseFile;
import com.example.course_learning_be.enums.CourseLevel;
import com.example.course_learning_be.service.CloudinarySecurityService;
import com.example.course_learning_be.service.CourseService;
import com.example.course_learning_be.service.CurriculumService;
import com.example.course_learning_be.service.LessonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {

  private final CourseService courseService;
  private final CurriculumService curriculumService;
  private final LessonService lessonService;
  private final CloudinarySecurityService cloudinarySecurityService;

//  @PostMapping("/AddCourse")
//  ApiResponse<Boolean> addCourse(@RequestBody CourseRequest courseRequest) {
//    var result = courseService.addCourse(courseRequest);
//    return ApiResponse.<Boolean>builder()
//        .data(result)
//        .build();
//  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createCourseWithFullImages(
      @RequestPart("data") String requestJson,
      @RequestPart(value = "pinImage") MultipartFile pinImage,
      @RequestPart(value = "previewImages") MultipartFile[] previewImages)
      throws JsonProcessingException {

    ObjectMapper objectMapper = new ObjectMapper();
    CourseRequestDTO requestDTO = objectMapper.readValue(requestJson, CourseRequestDTO.class);
    requestDTO.setPinImage(pinImage);
    requestDTO.setPreviewImage(previewImages);

    CourseResponseDTO res = courseService.create(requestDTO);

    return ResponseEntity.ok(res);
  }


  //  @GetMapping("/{courseId}")
//  ApiResponse<CourseResponse> getCourseById(@PathVariable String courseId) {
//    var result = courseService.getCourseById(courseId);
//    return ApiResponse.<CourseResponse>builder()
//        .data(result)
//        .build();
//  }
  @GetMapping
  public ResponseEntity<PageResponse<CourseResponse>> getAllCourses(
      @RequestParam(defaultValue = "0", required = false) int pageNo,
      @RequestParam(required = true) int pageSize,
      @RequestParam(required = false, defaultValue = "null") String sortBy
  ) {
    PageResponse<CourseResponse> response = courseService.getAllCourses(pageNo, pageSize, sortBy);
    return ResponseEntity.ok(response);
  }
  @PostMapping("/{courseId}/curriculum")
  public ResponseEntity<CurriculumResponseDTO> createCurriculum(
       @RequestBody CurriculumRequestDTO requestDTO,
      @PathVariable String courseId) {

    CurriculumResponseDTO res = curriculumService.create(requestDTO, courseId);
    return ResponseEntity.ok(res);
  }

  @GetMapping("/{courseId}/curriculums/lessons")
  public ResponseEntity<?> getCourseDetailsForActualLearning(@PathVariable String courseId) {
    var res = courseService.getDetailWithCurriculumAndLesson(courseId);
    return ResponseEntity.ok().body(res);

  }
  @PutMapping("/{courseId}")
  public ResponseEntity<?> updateCourse(@PathVariable String courseId,
      @RequestPart(value = "data", required = false) String requestJson,
      @RequestPart(value = "pinImage", required = false) MultipartFile pinImage,
      @RequestPart(value = "previewImages", required = false) MultipartFile[] previewImages) throws JsonProcessingException {

    ObjectMapper objectMapper = new ObjectMapper();
    CourseRequestDTO requestDTO = objectMapper.readValue(requestJson, CourseRequestDTO.class);
    requestDTO.setPreviewImage(previewImages);
    requestDTO.setPinImage(pinImage);

    CourseResponseDTO res = courseService.updateCourse(courseId, requestDTO);
    return ResponseEntity.ok().body(res);
  }
  @DeleteMapping("/{courseId}")
  public ResponseEntity<?> deleteCourse(@PathVariable String courseId) {
    courseService.delete(courseId);
    return ResponseEntity.ok().body(null);
  }
  @GetMapping("/{courseId}/curriculums/lessons/preview")
  public ResponseEntity<?> getCourseDetailsForPreview(@PathVariable String courseId) {
    var res = courseService.getDetailWithCurriculumAndLessonPreview(courseId);
    return ResponseEntity.ok().body(res);

  }
  @GetMapping("/curriculums")
  public ResponseEntity<?> getAllDetailWithCurriculum(
      @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
      @RequestParam(name = "level", required = false) CourseLevel level
      ) {
    var res = courseService.getAllDetailWithCurriculum(limit, level);
    return ResponseEntity.ok().body(res);

  }

//  @GetMapping("/{courseId}/lesson/{lessonId}")
//  public ResponseEntity<?> getLessonLearning(@PathVariable String courseId, @PathVariable String lessonId) {
//
//    var res = lessonService.getLessonLearning(courseId, lessonId);
//
//    return ResponseEntity.ok(res);
//  }
  @GetMapping("/{courseId}/lesson/{lessonId}")
  public ResponseEntity<?> getLessonLearning(@PathVariable String courseId, @PathVariable String lessonId) {

    var res = lessonService.getLessonLearning(courseId, lessonId);
    return ResponseEntity.ok(res);
  }

  @GetMapping("/signed-url")
  public ResponseEntity<Map<String, String>> getSignedVideoUrl(@RequestParam String videoId) {
    // Ví dụ expire 1 giờ
    String signedUrl = cloudinarySecurityService.generateSignedUrl(videoId, 3600);

    Map<String, String> res = new HashMap<>();
    res.put("videoUrl", signedUrl);
    return ResponseEntity.ok(res);
  }
  @PostMapping("/exercise/upload")
  public ResponseEntity<Map<String, String>> uploadExerciseFile(@RequestParam("file") MultipartFile file) throws IOException {
    ExerciseFile exerciseFile = lessonService.uploadExerciseFile(file);

    Map<String, String> result = new HashMap<>();
    result.put("exerciseFileId", exerciseFile.getPublicId());
    result.put("url", exerciseFile.getUrl());

    return ResponseEntity.ok(result);
  }


}
