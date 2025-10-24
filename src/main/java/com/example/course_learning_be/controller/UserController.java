package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.request.UserRegisterRequest;
import com.example.course_learning_be.dto.response.ApiResponse;
import com.example.course_learning_be.dto.response.AuthenticationResponse;
import com.example.course_learning_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")

public class UserController {

  private final UserService userService;
  @PostMapping("/UserRegister")
  public ApiResponse<Boolean> createUser(@RequestBody UserRegisterRequest userRegisterRequest) {
    var result = userService.userRegister(userRegisterRequest);
    return ApiResponse.<Boolean>builder()
        .data(result)
        .message(result ? "User registered successfully" : "User registration failed")
        .build();
  }

  @PostMapping("/UserLogin")
  ApiResponse<AuthenticationResponse> loginUser(@RequestBody UserRegisterRequest userRegisterRequest) {
    var result = userService.userLogin(userRegisterRequest.getFullName(), userRegisterRequest.getPassword());
    return ApiResponse.<AuthenticationResponse>builder()
        .data(result)
        .build();
  }

}
