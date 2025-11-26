package com.example.course_learning_be.service;

import com.example.course_learning_be.Util.ArrayUtils;
import com.example.course_learning_be.dto.request.CurriculumRequestDTO;
import com.example.course_learning_be.dto.response.CurriculumResponseDTO;
import com.example.course_learning_be.entity.Course;
import com.example.course_learning_be.entity.Curriculum;
import com.example.course_learning_be.entity.Lesson;
import com.example.course_learning_be.mapper.CurriculumMapper;
import com.example.course_learning_be.repository.CourseRepository;
import com.example.course_learning_be.repository.CurriculumRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
@Service
@RequiredArgsConstructor
public class CurriculumService {
  private final CurriculumRepository curriculumRepository;
  private final CurriculumMapper curriculumMapper;
  private final CourseRepository courseRepository;

  @Transactional
  public CurriculumResponseDTO create(CurriculumRequestDTO requestDTO, String courseId) {
    Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found "));
    Curriculum curriculum = curriculumMapper.fromRequestDTOToEntity(requestDTO, course);

    saveToCourse(curriculum, course);

    return curriculumMapper.fromEntityToResponseDTO(curriculum);
  }

  protected void saveToCourse(Curriculum curriculum, Course course) {
    int right = 0;
    if (curriculum.getPosition() != 1) {
      right = curriculum.getPosition() - 1;
    }

    curriculumRepository.save(curriculum);
    course.addByPosSmartWay(filterData(curriculum), right);
    courseRepository.save(course);
  }

  // nếu filterData chưa có thì tạm mock lại như sau:
  private Curriculum filterData(Curriculum curriculum) {
    return Curriculum.builder()
        .id(curriculum.getId())
        .title(curriculum.getTitle())
        .description(curriculum.getDescription())
        .position(curriculum.getPosition())
        .courseId(curriculum.getCourseId())
        .build();
  }

  public void addLessonByPosSmartWay(Curriculum curriculum, Lesson lesson, int right) {
    int pLeft = 0;
    int pRight = 0;
    if (right == 0) {
      pRight = ArrayUtils.getFree(curriculum.getOrderKeeper(), 0);
    } else {
      pLeft = ArrayUtils.getFree(curriculum.getOrderKeeper(), right - 1);
      pRight = ArrayUtils.getFree(curriculum.getOrderKeeper(), right);
    }

    int mid = pLeft + pRight - pLeft / 2;

    curriculum.getLessons().put(mid, lesson);

    curriculum.refreshOrder();
    curriculum.refreshKeeper();

  }

  public List<CurriculumResponseDTO> getAllCurriculumInCourse(String courseId) {
    Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
    AtomicInteger index = new AtomicInteger(1);

    //get ordered curriculums from the TreeMap
    List<Curriculum> orderedCurriculums = new ArrayList<>(course.getCurriculums());

    //map curriculum ID to fetched Curriculum
    List<String> ids = orderedCurriculums.stream().map(Curriculum::getId).toList();
    Map<String, Curriculum> fetched = curriculumRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(Curriculum::getId, Function.identity()));

    //rebuild ordered list using original order
    return orderedCurriculums.stream()
        .map(c -> fetched.get(c.getId()))
        .map(c -> curriculumMapper.fromEntityToResponseDTOWithPosition(c, index.getAndIncrement()))
        .toList();
  }

  public CurriculumResponseDTO update(String curriculumId, CurriculumRequestDTO requestDTO) {
    Curriculum curriculum = curriculumRepository.findById(curriculumId).orElseThrow(() -> new RuntimeException("Course not found"));

    curriculumMapper.updateEntityFromRequestDTO(curriculum, requestDTO);
    curriculumRepository.save(curriculum);
    return curriculumMapper.fromEntityToResponseDTO(curriculum);
  }
}
