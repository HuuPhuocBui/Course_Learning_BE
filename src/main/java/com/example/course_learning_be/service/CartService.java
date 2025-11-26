package com.example.course_learning_be.service;

import com.example.course_learning_be.Util.SecurityUtil;
import com.example.course_learning_be.dto.request.AddToCartRequestDTO;
import com.example.course_learning_be.dto.response.CartItemResponseDTO;
import com.example.course_learning_be.dto.response.CartResponseDTO;
import com.example.course_learning_be.entity.Cart;
import com.example.course_learning_be.entity.Course;
import com.example.course_learning_be.entity.User;
import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import com.example.course_learning_be.repository.CartRepository;
import com.example.course_learning_be.repository.CourseRepository;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {
  private final SecurityUtil securityUtil;
  private final CartRepository cartRepository;
  private final CourseRepository courseRepository;
  private final OrderService orderService;
  public void addToCart(AddToCartRequestDTO requestDTO) {
    User user = securityUtil.getCurrentUser();

    // Tìm cart hiện có hoặc tạo mới
    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseGet(() -> {
          Cart newCart = Cart.builder()
              .userId(user.getId())
              .cartItems(new HashSet<>())
              .build();
          return cartRepository.save(newCart);
        });

    // Lấy course
    Course course = courseRepository.findById(requestDTO.getCourseId())
        .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));

    // Thêm item vào cart
    cart.addItemToCart(course.getId());

    // Lưu lại cart
    cartRepository.save(cart);
  }

  public CartResponseDTO getCartByUserId() {
    User user = securityUtil.getCurrentUser();

    Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));

    List<String> courseIds = cart.getCartItems().stream().toList();
    List<Course> courses = courseRepository.findAllById(courseIds);
    var res = courses.stream().map(c -> {
          return CartItemResponseDTO.builder()
              .courseId(c.getId())
              .courseName(c.getTitle())
              .coursePrice(c.getPrice())
              .numberOfLessons(c.getDuration())
              .build();
        }
    ).toList();

    return CartResponseDTO.builder()
        .cartId(cart.getId())
        .cartItems(res)
        .userId(cart.getUserId())
        .productCount(cart.getCartItems().size())
        .totalPrice(this.calculateTotalAmount(cart))
        .build();
  }

  public long calculateTotalAmount(Cart cart) {
    List<Course> courses = courseRepository.findAllById(cart.getCartItems().stream().toList());

    return courses.stream().mapToLong(Course::getPrice).sum();
  }
  public void deleteItem(String itemCourseId) {
    User user = securityUtil.getCurrentUser();

    Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
    cart.removeItemFromCart(itemCourseId);
    cartRepository.save(cart);

  }
  @Transactional
  public String purchaseAll() throws UnsupportedEncodingException {
    User user = securityUtil.getCurrentUser();
    String userId = user.getId();

    Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));

    List<String> courseIds = cart.getCartItems().stream().toList();

    String paymentUrl = orderService.create(courseIds, calculateTotalAmount(cart), user);

    cart.cleanCart();

    return paymentUrl;

  }

}
