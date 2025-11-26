package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

}
