package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.Course;
import com.example.course_learning_be.entity.Curriculum;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CurriculumRepository extends MongoRepository<Curriculum, String> {
  List<Curriculum> findByCourseId(String courseId);
}
