package com.example.course_learning_be.service;

import com.example.course_learning_be.entity.Video;
import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import com.example.course_learning_be.repository.VideoRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoSecurityService {
  private final CloudinarySecurityService cloudinarySecurityService;
  private final VideoRepository videoRepository;

//  public String getSecureVideoUrl(String videoId, String userId) {
//    Video video = videoRepository.findById(videoId)
//        .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
//
//    return cloudinarySecurityService.generateSignedUrl(
//        extractPublicId(video.getVideoUrl())
//    );
//  }
//
//  public String getSecureThumbnailUrl(String videoId, String userId) {
//    Video video = videoRepository.findById(videoId)
//        .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
//
//    return cloudinarySecurityService.generateSignedUrl(
//        extractPublicId(video.getThumbnailUrl())
//    );
//  }

  private String extractPublicId(String url) {
    // Extract public_id từ Cloudinary URL
    Pattern pattern = Pattern.compile("/([^/]+)\\.[^/.]+$");
    Matcher matcher = pattern.matcher(url);
    if (matcher.find()) {
      return matcher.group(1);
    }
    throw new AppException(ErrorCode.INVALID_INPUT);
  }
}
