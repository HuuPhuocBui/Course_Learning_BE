package com.example.course_learning_be.mapper;

import static com.example.course_learning_be.mapper.DTOMapper.applyIfNotNull;

import com.example.course_learning_be.dto.request.UserUpdateRequestDTO;
import com.example.course_learning_be.dto.response.UserResponseDTO;
import com.example.course_learning_be.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class UserMapper {
//  private UserMapper() {
//    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
//  }
  public static UserResponseDTO mapToUserDto(User user){
    return new UserResponseDTO(
        user.getId(),
        user.getEmail(),
        user.getAvatar(),
        user.getAvatarUrl(),
        user.getFullName(),
        user.getRole(),
        "VIEW_DETAIL"
    );
  }
  public static List<UserResponseDTO> mapToUserDtoList(List<User> users) {
    return users.stream()
        .map(UserMapper::mapToUserDto)
        .collect(Collectors.toList());
  }
  public void updateEntityFromRequestDTO(User user, UserUpdateRequestDTO requestDTO) {
    applyIfNotNull(requestDTO.getNewFullName(), user::setFullName);
    applyIfNotNull(requestDTO.getNewEmail(), user::setEmail);
    applyIfNotNull(requestDTO.getNewAvatarUrl(), user::setAvatarUrl);

  }

  public UserResponseDTO fromEntityToResponseDTO(User user) {
    return UserResponseDTO.builder()
        .email(user.getEmail())
        .fullName(user.getFullName())
        .avatar(user.getAvatarUrl())
        .role(user.getRole())
        .build();

  }

  }
