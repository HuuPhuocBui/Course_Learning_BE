package com.example.course_learning_be.mapper;

import com.example.course_learning_be.dto.response.ChunkUploadResponseDTO;
import com.example.course_learning_be.entity.Chunk;
import com.example.course_learning_be.entity.Video;

public class ChunkMapper {
  public static ChunkUploadResponseDTO fromEntityToResponse(Chunk chunk, Video video) {
    return ChunkUploadResponseDTO.builder()
        .chunkIndex(chunk.getIndex())
        .numberOfChunkLeft(video.getNumberOfChunkLeft())
        .uploadCompleted(video.isUploadComplete())
        .chunkUrl(chunk.getUrl())
        .chunkSize(chunk.getChunkSize())
        .thumbnailUrl(chunk.getThumbnailUrl())
        .build();
  }
}
