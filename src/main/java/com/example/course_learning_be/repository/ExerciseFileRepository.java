package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.ExerciseFile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExerciseFileRepository extends MongoRepository<ExerciseFile, String> {

}
