package com.example.course_learning_be.repository;

import java.util.Collection;
import java.util.Optional;
import org.springframework.data.mongodb.core.query.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoService {

  private final MongoTemplate mongoTemplate;

  public MongoTemplate getMongoTemplate() {
    return this.mongoTemplate;
  }

  public <T> T save(T data) {
    return this.getMongoTemplate().save(data);
  }

  public <T> Collection<T> insertAll(Collection<T> data) {
    return this.getMongoTemplate().insertAll(data);
  }

  public <T> Optional<T> findOne(Query query, Class<T> entityClass) {
    T result = this.getMongoTemplate().findOne(query, entityClass);
    return Optional.ofNullable(result);
  }

}

