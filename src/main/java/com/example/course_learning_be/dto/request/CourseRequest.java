package com.example.course_learning_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    private String title;
    private String description;
    private String duration;
    private String level;
    private String authorName;
    private String price;
    private String imageUrl;
}
