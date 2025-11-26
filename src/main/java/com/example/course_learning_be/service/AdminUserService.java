package com.example.course_learning_be.service;

import com.example.course_learning_be.dto.response.UserResponseDTO;
import com.example.course_learning_be.entity.User;
import com.example.course_learning_be.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService {
  private final UserRepository userRepository;

  public List<UserResponseDTO> getAllUsers() {
    List<User> users = userRepository.findAll();
    return users.stream()
        .map(user -> UserResponseDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .avatar(user.getAvatar())
            .avatarUrl(user.getAvatarUrl())
            .role(user.getRole())
            .action("VIEW_DETAIL") // field thêm cho FE xử lý
            .build())
        .collect(Collectors.toList());
  }
}
