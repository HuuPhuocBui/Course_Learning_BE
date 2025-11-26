package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {
  Payment findByTransactionId(String transactionId);
}
