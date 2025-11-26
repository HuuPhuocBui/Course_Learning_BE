package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.Lesson;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LessonRepository  extends MongoRepository<Lesson, String> {
}
