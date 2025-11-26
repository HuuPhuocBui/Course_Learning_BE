package com.example.course_learning_be.entity;

import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
@Document(collection = "videos")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class Video {
  @Id
  private String id;
  @Field(name = "title")
  private String title;

  @Field(name = "description")
  private String description;

  @Field(name = "length")
  private String length;

  @Field(name = "size")
  private double size;

  @Field(name = "video_url")
  private String videoUrl;

  @Field(name = "public_id")
  private String publicId;

  private String duration;

  private String thumbnailUrl;

  @Field(name = "expectedTotalChunk")
  private int expectedTotalChunk;

  @Builder.Default
  @Field(name = "chunkCount")
  private int chunkCount = 0;
  @Field(name = "chunks")
  //tai sao khi khoi tao builder.default thi moi lan add chunk cu bi tao moi map lien tuc, key bi xoa lien tuc
  private NavigableMap<Long, Chunk> chunks;
  public Video() {
    this.id = UUID.randomUUID().toString();
  }
  public void addChunk(Chunk chunk) {


    if (chunk.getIndex() > expectedTotalChunk - 1) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    if (chunks == null || chunks.isEmpty()) {
      chunks = new TreeMap<>();
      chunkCount = 0;
    }

    if (!chunks.containsKey(chunk.getIndex())) {
      increment();
    } else {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }
    chunks.put(chunk.getIndex(), chunk);
  }
  public synchronized void increment() {
    this.chunkCount++;
  }
  public void clearChunks() {
    chunks = new TreeMap<>();
    chunkCount = 0;
  }
  public long getNumberOfChunkLeft() {
    return this.getExpectedTotalChunk() - this.getChunkCount();
  }
  public boolean isUploadComplete() {
    long left = this.getExpectedTotalChunk() - this.getChunkCount();

    if (left == 0) {
      return true;
    } else if (left < 0) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }
    return false;
  }
}
