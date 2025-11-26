package com.example.course_learning_be.service;

import com.example.course_learning_be.Util.SecurityUtil;
import com.example.course_learning_be.dto.request.CourseRequestDTO;
import com.example.course_learning_be.dto.response.CourseResponse;
import com.example.course_learning_be.dto.response.CourseResponseDTO;
import com.example.course_learning_be.dto.response.PageResponse;
import com.example.course_learning_be.entity.Course;
import com.example.course_learning_be.entity.User;
import com.example.course_learning_be.enums.CourseLevel;
import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import com.example.course_learning_be.mapper.CourseMapper;
import com.example.course_learning_be.repository.CourseRepository;
import com.example.course_learning_be.repository.MongoService;
import com.example.course_learning_be.repository.UserRepository;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class CourseService {
  private final MongoService mongoService;
  private final CourseRepository courseRepository;
  private final SecurityUtil securityUtil;
  private final CourseMapper courseMapper;
  private final CloudinaryService cloudinaryService;
  private final CurriculumService curriculumService;
  private final LessonService lessonService;
  private final UserRepository userRepository;

//  public Boolean addCourse(CourseRequest courseRequest){
//    String currentUserId = SecurityUtil.getCurrentUserId();
//    Course course = new Course();
//    course.setTitle(courseRequest.getTitle());
//    course.setDescription(courseRequest.getDescription());
//    course.setDuration(courseRequest.getDuration());
//    course.setLevel(courseRequest.getLevel());
//    course.setPrice(courseRequest.getPrice());
//    course.setAuthorName(courseRequest.getAuthorName());
//    Course saveCourse = mongoService.save(course);
//    return saveCourse != null;
//  }

  public CourseResponseDTO updateCourse(String courseId, CourseRequestDTO requestDTO) {
    Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

    prepareImages(requestDTO);

    courseMapper.updateEntityFromRequestDTO(course, requestDTO);
    courseRepository.save(course);

    return courseMapper.fromEntityToResponse(course);
  }

  private String[] getNullPropertyNames(Object source) {
    return Arrays.stream(BeanUtils.getPropertyDescriptors(source.getClass()))
        .map(PropertyDescriptor::getName)
        .filter(name -> {
          try {
            Object value = new PropertyDescriptor(name, source.getClass())
                .getReadMethod().invoke(source);
            return Objects.isNull(value);
          } catch (Exception e) {
            return false;
          }
        })
        .toArray(String[]::new);
  }

  public Boolean deleteCourse(String courseId){
    boolean deleted = mongoService.deleteById(courseId, Course.class);
    if (!deleted) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }
    return true;

  }

//  public CourseResponse getCourseById(@PathVariable String courseId) {
//    Course course = mongoService.findById(courseId, Course.class)
//        .orElseThrow(() -> new RuntimeException("Course not found"));
//    return CourseMapper.mapToCourseDto(course);
//  }

  public PageResponse<CourseResponse> getAllCourses(int pageNo, int pageSize, String sortBy) {
    int realPage = pageNo > 0 ? pageNo - 1 : 0;

    Sort sort = Sort.unsorted();
    if (sortBy != null && !sortBy.isBlank() && !sortBy.equalsIgnoreCase("null")) {
      sort = Sort.by(Sort.Direction.ASC, sortBy);
    }

    Pageable pageable = PageRequest.of(realPage, pageSize, sort);
    Page<Course> page = courseRepository.findAll(pageable);

    List<CourseResponse> data = page.stream()
        .map(CourseMapper::mapToCourseDto)
        .toList();

    return PageResponse.<CourseResponse>builder()
        .data(data)
        .pageInfo(PageResponse.PageInfo.builder()
            .currentPage(page.getNumber() + 1)
            .totalItems(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .hasNextPage(page.hasNext())
            .hasPreviousPage(page.hasPrevious())
            .build())
        .build();
  }

  public CourseResponseDTO create(CourseRequestDTO requestDTO) {
    User user = securityUtil.getCurrentUser();

    prepareImages(requestDTO);

    Course course = courseMapper.fromCreateRequestToEntity(requestDTO, user);
    courseRepository.save(course);

    return courseMapper.fromEntityToResponse(course);
  }

  private void prepareImages(CourseRequestDTO requestDTO) {
    // Upload ảnh pin
    String pinImageUrl = null;
    if (requestDTO.getPinImage() != null && !requestDTO.getPinImage().isEmpty()) {
      pinImageUrl = cloudinaryService.uploadFile(requestDTO.getPinImage());
    }
    requestDTO.setPinImageUrl(pinImageUrl);

    // Upload ảnh preview
    List<String> previewImageUrls = new ArrayList<>();
    MultipartFile[] previewImages = requestDTO.getPreviewImage();

    if (previewImages != null && previewImages.length > 0) {
      String pinImgName = requestDTO.getPinImage() != null ? requestDTO.getPinImage().getOriginalFilename() : null;

      List<MultipartFile> filtered = new ArrayList<>();
      for (MultipartFile file : previewImages) {
        if (pinImgName == null || !file.getOriginalFilename().equals(pinImgName)) {
          filtered.add(file);
        }
      }

      List<String> uploadedUrls = cloudinaryService.uploadFiles(filtered.toArray(new MultipartFile[0]));
      previewImageUrls.addAll(uploadedUrls);

      if (pinImageUrl != null) previewImageUrls.add(pinImageUrl);
    }

    requestDTO.setPreviewImageUrls(previewImageUrls);
  }

  public CourseResponseDTO getDetailWithCurriculumAndLesson(String courseId) {
    Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

    return CourseResponseDTO.builder()
        .id(course.getId())
        .title(course.getTitle())
        .description(course.getDescription())
        .pinImageUrl(course.getPinImageUrl())
        .curriculums(traceCurriculumInCourse(courseId))
        .price(course.getPrice())
        .accessLevel(course.getAccessLevel())
        .duration(course.getDuration())
        .level(course.getLevel())
        .authorName(course.getOwner().getFullName())
        .language(course.getLanguage())
        .content(course.getContent())
        .previewImageUrls(course.getPreviewImageUrls())
        .build();

  }

  private List<CourseResponseDTO.CurriculumDTO> traceCurriculumInCourse(String courseId) {
    return curriculumService.getAllCurriculumInCourse(courseId).stream()
        .map(c -> CourseResponseDTO.CurriculumDTO.builder()
            .id(c.getCurriculumId())
            .title(c.getTitle())
            .description(c.getDescription())
            .lessons(traceLessonInCurriculum(c.getCurriculumId()))
            .build())
        .toList();
  }

  private List<CourseResponseDTO.LessonDTO> traceLessonInCurriculum(String curriculumId) {
    return lessonService.getAllInCurriculum(curriculumId).stream()
        .map(l -> CourseResponseDTO.LessonDTO.builder()
            .id(l.getLessonId())
            .duration(l.getDuration())
            .title(l.getTitle())
            .build())
        .toList();
  }

  public void delete(String id) {
    courseRepository.deleteById(id);
  }

  public List<CourseResponseDTO> getAllLimit6() {
    List<Course> courses = courseRepository.findAll();

    return courses.stream()
        .map(courseMapper::fromEntityToResponse)
        .limit(6)
        .toList();
  }

  public CourseResponseDTO getDetailWithCurriculumAndLessonPreview(String courseId) {
    Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

    return CourseResponseDTO.builder()
        .id(course.getId())
        .title(course.getTitle())
        .description(course.getDescription())
        .pinImageUrl(course.getPinImageUrl())
        .curriculums(traceCurriculumInCoursePreview(courseId))
        .price(course.getPrice())
        .accessLevel(course.getAccessLevel())
        .duration(course.getDuration())
        .level(course.getLevel())
        .authorName(course.getOwner().getFullName())
        .language(course.getLanguage())
        .content(course.getContent())
        .previewImageUrls(course.getPreviewImageUrls())
        .build();
  }

  private List<CourseResponseDTO.CurriculumDTO> traceCurriculumInCoursePreview(String courseId) {
    return curriculumService.getAllCurriculumInCourse(courseId).stream()
        .map(c -> CourseResponseDTO.CurriculumDTO.builder()
            .title(c.getTitle())
            .description(c.getDescription())
            .lessons(traceLessonInCurriculum(c.getCurriculumId()))
            .build())
        .toList();
  }
//  public List<CourseResponseDTO> getAllDetailWithCurriculum(int limit, CourseLevel level) {
//    List<Course> courseList = courseRepository.findAll().stream().limit(100).toList();
//    List<CourseResponseDTO> output = new ArrayList<>();
//    for (Course course : courseList) {
//      output.add(courseMapper.fromEntityToCourseAndCurriculumResponseDTO(course, generateCurriculumInfo(course)));
//    }
//    return output;
//  }
public List<CourseResponseDTO> getAllDetailWithCurriculum(int limit, CourseLevel level) {

  // Lấy toàn bộ danh sách courses
  List<Course> courseList = courseRepository.findAll();

  // Filter theo level
  if (level != null) {
    courseList = courseList.stream()
        .filter(c -> level.equals(c.getLevel()))
        .toList();
  }

  // Filter theo category (nếu bạn có trường này)

  // Apply limit
  courseList = courseList.stream().limit(limit).toList();

  // Map sang DTO
  List<CourseResponseDTO> output = new ArrayList<>();
  for (Course course : courseList) {
    output.add(
        courseMapper.fromEntityToCourseAndCurriculumResponseDTO(
            course,
            generateCurriculumInfo(course)
        )
    );
  }

  return output;
}


  private List<CourseResponseDTO.CurriculumDTO> generateCurriculumInfo(Course course) {
    return curriculumService.getAllCurriculumInCourse(course.getId()).stream()
        .map(c -> CourseResponseDTO.CurriculumDTO.builder()
            .id(c.getCurriculumId())
            .title(c.getTitle())
            .description(c.getDescription())
            .build())
        .limit(100)
        .toList();
  }

  public List<Course> getAllCourseOfCurrentUser() {
    String userId = securityUtil.getCurrentUser().getId();
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Course not found"));
    List<String> userCourseIds = user.getCourses().stream().toList();
    return courseRepository.findAllById(userCourseIds);
  }




}
