package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.request.AddToCartRequestDTO;
import com.example.course_learning_be.dto.request.ContactRequestDTO;
import com.example.course_learning_be.dto.response.BaseResponseList;
import com.example.course_learning_be.dto.response.ContactResponseDTO;
import com.example.course_learning_be.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contact")
@Validated
@Slf4j(topic = "CONTACT-CONTROLLER")
@RequiredArgsConstructor
public class ContactController {
  private final ContactService contactService;
  @PostMapping("/user")
  public ResponseEntity<?> contactUser(@RequestBody ContactRequestDTO contactRequestDTO) {
    contactService.sendMessage(contactRequestDTO);

    return ResponseEntity.ok().body(null);
  }
  @GetMapping("/admin")
  public ResponseEntity<BaseResponseList<ContactResponseDTO>> getAllReview() {
    BaseResponseList<ContactResponseDTO> res = contactService.getAllReview();
    return ResponseEntity.ok(res);
  }


}
