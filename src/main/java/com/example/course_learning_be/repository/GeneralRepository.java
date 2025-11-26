package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.General;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GeneralRepository extends MongoRepository<General, String> {
  General findTopByOrderByIdDesc();

}
