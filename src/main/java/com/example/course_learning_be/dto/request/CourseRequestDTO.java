package com.example.course_learning_be.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDTO {
    private String id;
    private String title;
    private String description;
    private String content;
    private String duration;
    private String level;
    private String accessLevel;
    private String language;
    private String pinImageUrl;
    private List<String> previewImageUrls;
    private long price;
    private MultipartFile[] previewImage;
    private MultipartFile pinImage;
}
