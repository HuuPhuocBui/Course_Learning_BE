package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepository extends MongoRepository<Video, String> {

}
