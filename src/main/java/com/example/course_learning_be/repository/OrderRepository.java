package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.Order;
import com.example.course_learning_be.enums.OrderStatus;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
  List<Order> findByUserId(String userId);
  List<Order> findByStatus(OrderStatus orderStatus);
  Page<Order> findByStatus(OrderStatus orderStatus, Pageable pageable);
  long countByStatus(OrderStatus status);
  List<Order> findTop10ByOrderByCreatedAtDesc();

  @Query("{ 'paid_at': { $gte: ?0, $lt: ?1 } }")
  List<Order> findByPaidAtBetween(Instant from, Instant to);

  @Query("{ 'raised_at': { $gte: ?0, $lt: ?1 } }")
  List<Order> findByRaisedAtBetween(Instant from, Instant to);
}
