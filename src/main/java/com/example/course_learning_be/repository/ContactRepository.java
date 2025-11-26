package com.example.course_learning_be.repository;

import com.example.course_learning_be.entity.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends MongoRepository<Contact, String> {

}
