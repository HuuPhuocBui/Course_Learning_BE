package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.request.UserRegisterRequest;
import com.example.course_learning_be.dto.response.ApiResponse;
import com.example.course_learning_be.dto.response.AuthenticationResponse;
import com.example.course_learning_be.entity.User;
import com.example.course_learning_be.repository.UserRepository;
import com.example.course_learning_be.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")

public class AuthenticationController {

  private final UserService userService;
  private final UserRepository userRepository;
//  @PostMapping("/UserRegister")
//  public ApiResponse<Boolean> createUser(@RequestBody UserRegisterRequest userRegisterRequest) {
//    var result = userService.userRegister(userRegisterRequest);
//    return ApiResponse.<Boolean>builder()
//        .data(result)
//        .message(result ? "User registered successfully" : "User registration failed")
//        .build();
//  }

  @PostMapping("/UserLogin")
  ApiResponse<AuthenticationResponse> loginUser(@RequestBody UserRegisterRequest userRegisterRequest) {
    var result = userService.userLogin(userRegisterRequest.getFullName(), userRegisterRequest.getPassword());
    return ApiResponse.<AuthenticationResponse>builder()
        .data(result)
        .build();
  }

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> loginAdmin(@RequestBody UserRegisterRequest userRegisterRequest) {
    AuthenticationResponse result = userService.adminLogin(userRegisterRequest.getEmail(), userRegisterRequest.getPassword());
    return ResponseEntity.ok(result);
  }
@PostMapping("/UserRegister")
public ApiResponse<Boolean> createUser(
    @RequestHeader("Authorization") String authHeader,
    @RequestBody UserRegisterRequest userRegisterRequest
) throws FirebaseAuthException {

  if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    throw new RuntimeException("Missing Firebase token");
  }

  String idToken = authHeader.substring(7);

  // 1️⃣ Verify Firebase token
  FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

  String uid = decodedToken.getUid();
  String email = decodedToken.getEmail();

  // 2️⃣ Lưu thông tin bổ sung vào DB (không lưu password)
  User user = new User();
  user.setId(uid);
  user.setEmail(email);
  user.setRole("USER");
  user.setFullName(userRegisterRequest.getFullName());
  userRepository.save(user);

  return ApiResponse.<Boolean>builder()
      .data(true)
      .message("User registered successfully")
      .build();
}

//  @PostMapping("/login")
//  public ResponseEntity<?> login(
//      @RequestHeader("Authorization") String authHeader,
//      @RequestParam(required = false) String deviceId // deviceId từ frontend
//  ) throws Exception {
//    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//      throw new RuntimeException("Missing token");
//    }
//
//    // 1️⃣ Lấy Firebase ID Token từ header
//    String idToken = authHeader.substring(7);
//
//    // 2️⃣ Xác thực ID Token với Firebase
//    FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
//    String uid = decodedToken.getUid();
//    String email = decodedToken.getEmail();
//
//    // 3️⃣ Kick các thiết bị cũ (invalidate session cũ)
//    // Nếu muốn sử dụng Firebase để force logout, revoke refresh tokens
//    FirebaseAuth.getInstance().revokeRefreshTokens(uid);
//
//    // 4️⃣ Quản lý BE session (optional)
//    // Lưu deviceId + token + valid = true trong DB
//    // Invalid tất cả session cũ của user
//    userService.invalidateOldSessions(uid);
//
//    // 5️⃣ Tạo token BE mới
//    String beToken = userService.generateToken(uid, deviceId);
//
//    // 6️⃣ Trả về BE token và thông tin user
//    return ResponseEntity.ok(Map.of(
//        "accessToken", beToken,
//        "email", email,
//        "deviceId", deviceId
//    ));
//  }



}
