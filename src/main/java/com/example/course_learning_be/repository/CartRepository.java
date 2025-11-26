package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.Cart;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
  public Optional<Cart> findByUserId(String userId);
  void deleteAllByUserId(String userId);
}
