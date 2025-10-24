package com.example.course_learning_be.service;

import com.example.course_learning_be.dto.request.CourseRequest;
import com.example.course_learning_be.entity.Course;
import com.example.course_learning_be.repository.MongoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CourseService {
  private final MongoService mongoService;

  public Boolean addCourse(CourseRequest courseRequest){
    Course course = new Course();
    course.setTitle(courseRequest.getTitle());
    course.setDescription(courseRequest.getDescription());
    course.setDuration(courseRequest.getDuration());
    course.setLevel(courseRequest.getLevel());
    course.setPrice(courseRequest.getPrice());
    course.setAuthorName(courseRequest.getAuthorName());
    Course saveCourse = mongoService.save(course);
    return saveCourse != null;
  }

}
