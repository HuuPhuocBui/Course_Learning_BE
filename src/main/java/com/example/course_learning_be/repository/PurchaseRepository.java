package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.Purchase;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PurchaseRepository extends MongoRepository<Purchase, String> {
  List<Purchase> findAll();
  Optional<Purchase> findByBuyerIdAndCourseName(String buyerId, String courseName);
}
