package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.UserSession;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserSessionRepository extends MongoRepository<UserSession, String> {
  List<UserSession> findByUserIdAndValidTrue(String userId);
  UserSession findByToken(String token);
}
