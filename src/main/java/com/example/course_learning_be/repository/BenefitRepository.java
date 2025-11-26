package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.Benefit;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitRepository extends MongoRepository<Benefit, String> {

}
