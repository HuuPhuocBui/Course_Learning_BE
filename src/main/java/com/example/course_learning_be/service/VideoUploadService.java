package com.example.course_learning_be.service;

import com.example.course_learning_be.dto.response.ChunkUploadResponseDTO;
import com.example.course_learning_be.entity.Chunk;
import com.example.course_learning_be.entity.Video;
import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import com.example.course_learning_be.mapper.ChunkMapper;
import com.example.course_learning_be.repository.VideoRepository;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
@RequiredArgsConstructor
@Slf4j(topic = "VIDEO-UPLOAD-SERVICE")
public class VideoUploadService {
  private static final long MAXIMUM_CHUNK_UPLOAD_TIME_IN_MILSEC = 1500;
  private final VideoRepository videoRepository;
  private final VideoService videoService;
  private final VideoChunkService videoChunkService;
  public ChunkUploadResponseDTO uploadChunkWithTimeConstraint(MultipartFile file, String videoId, long chunkIndex) {

    ExecutorService executor = Executors.newSingleThreadExecutor();
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Course not found "));

    Callable<ChunkUploadResponseDTO> task = createChunkUploadTask(video, file, chunkIndex);
    Future<?> future = executor.submit(task);

    try {
      return (ChunkUploadResponseDTO) future.get(MAXIMUM_CHUNK_UPLOAD_TIME_IN_MILSEC, TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {
      future.cancel(true);
      videoService.deleteChunks(video);
      scheduler.schedule(() -> {
        if (!future.isDone()) {
          future.cancel(true);
          executor.shutdownNow();
        }
      }, 10, TimeUnit.SECONDS);

      throw new AppException(ErrorCode.INVALID_INPUT);

    } catch (Exception e) {
      videoService.deleteChunks(video);
      throw new AppException(ErrorCode.INVALID_INPUT);

    } finally {
      if (future.isDone()) {
        executor.shutdown();
      }
      scheduler.shutdown();
    }
  }


  private Callable<ChunkUploadResponseDTO> createChunkUploadTask(Video video, MultipartFile file, long chunkIndex) {
    return () -> {
      Chunk chunk = videoChunkService.chunkBuilder(file, chunkIndex);
      this.addChunkWithConsistency(video, chunk);

      if (Thread.currentThread().isInterrupted()) {
        throw new AppException(ErrorCode.INVALID_INPUT);
      }

      videoRepository.save(video);
      return ChunkMapper.fromEntityToResponse(chunk, video);
    };
  }
  private void addChunkWithConsistency(Video video, Chunk chunk) {
    try {
      video.addChunk(chunk);
    } catch (AppException e) {
      videoService.deleteChunks(video);
      throw new AppException(ErrorCode.INVALID_INPUT);
    } catch (Exception e) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }
  }

}
