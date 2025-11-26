package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.CourseStudent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseStudentRepository extends MongoRepository<CourseStudent, String> {

}
