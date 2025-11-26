package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.response.BaseResponseList;
import com.example.course_learning_be.dto.response.BenefitResponseDTO;
import com.example.course_learning_be.dto.response.ContactResponseDTO;
import com.example.course_learning_be.dto.response.CourseResponseDTO;
import com.example.course_learning_be.dto.response.TestimonialResponseDTO;
import com.example.course_learning_be.service.BenefitService;
import com.example.course_learning_be.service.ContactService;
import com.example.course_learning_be.service.CourseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/homepage")
@Validated
@Slf4j(topic = "HOMEPAGE-CONTROLLER")
@RequiredArgsConstructor
public class HomePageController {
  private final CourseService courseService;
  private final BenefitService benefitService;
  private final ContactService contactService;
  @GetMapping("/course")
  public ResponseEntity<?> getThirdPart() {
    List<CourseResponseDTO> res = courseService.getAllLimit6();
    return ResponseEntity.ok().body(res);
  }
  @GetMapping("/benefit")
  public ResponseEntity<?> getSecondPart() {
    List<BenefitResponseDTO<String>> res = benefitService.getAllLocalized();
    return ResponseEntity.ok().body(res);
  }
  @GetMapping("/review")
  public ResponseEntity<?> getAllReviewClient() {
    List<TestimonialResponseDTO> res = contactService.getAllReviewClient();
    return ResponseEntity.ok().body(res);
  }

}
