package com.example.course_learning_be.Util;
import com.example.course_learning_be.entity.User;
import com.example.course_learning_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
  private final UserRepository userRepository;
  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new RuntimeException("User not authenticated");
    }

    String email = authentication.getName(); // hoặc authentication.getPrincipal() nếu JWT chứa email/id

    return userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + email));
  }
  public static String getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    return authentication.getName();
  }
}
