package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.request.UserUpdateRequestDTO;
import com.example.course_learning_be.dto.response.UserResponseDTO;
import com.example.course_learning_be.service.CartService;
import com.example.course_learning_be.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
  private final UserService userService;
  private final CartService cartService;
  @GetMapping("/profile")
  public ResponseEntity<UserResponseDTO> getUserProfile() {
    UserResponseDTO result = userService.getUserProfile();
    return ResponseEntity.ok(result);
  }

  @GetMapping("/cart")
  public ResponseEntity<?> getUserCart() {
    var res = cartService.getCartByUserId();
    return ResponseEntity.ok().body(res);
  }

  @GetMapping("/courses")
  public ResponseEntity<?> getUserCourses() {

    var res = userService.getUserLearningCourse();
    return ResponseEntity.ok().body(res);
  }
  @PutMapping("/profile")
  public ResponseEntity<?> updateUserProfile(@RequestPart(name = "data") String requestJson) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    UserUpdateRequestDTO requestDTO = objectMapper.readValue(requestJson, UserUpdateRequestDTO.class);

    var res = userService.updateUserProfile(requestDTO);
    return ResponseEntity.ok().body(res);
  }
  @PutMapping("/avatar")
  public ResponseEntity<?> updateAvatar(@RequestParam("file") MultipartFile file) throws IOException {


    var res = userService.updateUserAvatar(file);
    return ResponseEntity.ok().body(res);
  }
}
