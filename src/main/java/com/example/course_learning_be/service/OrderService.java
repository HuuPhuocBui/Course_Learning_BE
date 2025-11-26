package com.example.course_learning_be.service;

import static com.example.course_learning_be.enums.AppConst.SORT_BY;

import com.example.course_learning_be.Util.SecurityUtil;
import com.example.course_learning_be.dto.request.OrderPaymentDTO;
import com.example.course_learning_be.dto.response.OrderGetResponseDTO;
import com.example.course_learning_be.dto.response.PageResponse;
import com.example.course_learning_be.entity.Course;
import com.example.course_learning_be.entity.Order;
import com.example.course_learning_be.entity.User;
import com.example.course_learning_be.enums.OrderStatus;
import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import com.example.course_learning_be.repository.CourseRepository;
import com.example.course_learning_be.repository.OrderRepository;
import com.example.course_learning_be.repository.UserRepository;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final PaymentService paymentService;
  private final UserRepository userRepository;
  private final SecurityUtil securityUtil;
  private final CourseRepository courseRepository;

  /**
   * Tạo order và thanh toán trực tiếp trong 1 service
   */
  // 1. Lọc khóa học đã mua
  public String create(List<String> courseIds, long amount, User user)
      throws UnsupportedEncodingException {
    List<String> validCourseIds = removeBoughtCourse(courseIds, user);
    Order order = getInitOrder(validCourseIds, user.getId(), amount);
    orderRepository.save(order);

    var dto = OrderService.parseToOrderPaymentDTO(order);

    return paymentService.createPayment(dto);

  }

  private List<String> removeBoughtCourse(List<String> courseId, User user) {

    return courseId.stream().filter(c -> !user.getCourses().contains(c)).toList();
  }


  static Order getInitOrder(List<String> courseIds, String userId, long totalAmount) {
    return Order.builder()
        .code(OrderService.generateOrderCode())
        .userId(userId)
        .courseIds(courseIds)
        .status(OrderStatus.PENDING)
        .totalAmount(totalAmount)
        .raisedAt(LocalDate.now())
        .createdAt(Instant.now())
        .build();
  }

  static Order getInitOrderOne(String courseId, String userId, long totalAmount) {
    return Order.builder()
        .code(OrderService.generateOrderCode())
        .userId(userId)
        .courseIds(List.of(courseId))
        .status(OrderStatus.PENDING)
        .totalAmount(totalAmount)
        .raisedAt(LocalDate.now())
        .createdAt(Instant.now())
        .build();
  }

  static OrderPaymentDTO parseToOrderPaymentDTO(Order order) {
    return OrderPaymentDTO.builder()
        .orderCode(order.getCode())
        .orderId(order.getId())
        .userId(order.getUserId())
        .courseIds(order.getCourseIds())
        .orderStatus(order.getStatus())
        .totalAmount(order.getTotalAmount())
        .createdAt(order.getCreatedAt())
        .build();

  }


  static String generateOrderCode() {
    String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
    String randomPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8)
        .toUpperCase();
    return String.format("%s-%s-%s", "ORD", datePart, randomPart);
  }

  static void addOtherInfoCaseSuccessful(Order order, String transactionId, String paymentMethod) {
    order.setStatus(OrderStatus.COMPLETE);
    order.setTransactionId(transactionId);
    order.setPaidAt(LocalDate.now());
    order.setPaymentMethod(paymentMethod);
  }

  public PageResponse<?> getAllOrderOfSystem(int pageNo, int pageSize, String sortBy, OrderStatus orderStatus) {
    int realPageNo = pageNo > 0 ? pageNo - 1 : 0;

    List<Sort.Order> sorts = new ArrayList<>();

    if (StringUtils.hasLength(sortBy)) {
      // firstName:asc|desc
      Pattern pattern = Pattern.compile(SORT_BY);
      Matcher matcher = pattern.matcher(sortBy);
      if (matcher.find()) {
        if (matcher.group(3).equalsIgnoreCase("asc")) {
          sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
        } else {
          sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
        }
      }
    }

    Pageable pageable = PageRequest.of(realPageNo, pageSize, Sort.by(sorts));

    Page<Order> page = null;
    if (orderStatus == null) {
      page = orderRepository.findAll(pageable);
    } else {
      page = orderRepository.findByStatus(orderStatus, pageable);
    }

    List<OrderGetResponseDTO> resList = page.stream().map(
        o -> OrderGetResponseDTO.builder()
            .id(o.getId())
            //.courseNames(courseService.getCourseNamesFromCourseIds(o.getCourseIds()))
            .buyerEmail(userRepository.findById(o.getUserId()).orElseThrow(() -> new RuntimeException("User not found")).getEmail())
            .totalAmount(o.getTotalAmount())
            .status(o.getStatus())
            .paidAt(o.getPaidAt())
            .paymentMethod(o.getPaymentMethod())
            .build()
    ).toList();

    if (pageNo == 0) {
      pageNo = 1;
    }
    return PageResponse.<OrderGetResponseDTO>builder()
        .data(resList)
        .pageInfo(PageResponse.PageInfo.builder()
            .currentPage(pageNo)
            .totalItems(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .hasNextPage(page.hasNext())
            .hasPreviousPage(page.hasPrevious())
            .build())
        .build();

  }

  public String buyRightNow(String courseId) throws UnsupportedEncodingException {
    User user = securityUtil.getCurrentUser();

    if (user.getCourses().contains(courseId)) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

    Order order = OrderService.getInitOrderOne(courseId, user.getId(), course.getPrice());
    orderRepository.save(order);
    OrderPaymentDTO dto = OrderService.parseToOrderPaymentDTO(order);

    return paymentService.createPayment(dto);
  }


}