package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.response.BaseResponseList;
import com.example.course_learning_be.dto.response.PageResponse;
import com.example.course_learning_be.dto.response.UserResponseDTO;
import com.example.course_learning_be.enums.OrderStatus;
import com.example.course_learning_be.service.AdminUserService;
import com.example.course_learning_be.service.OrderService;
import com.example.course_learning_be.service.OrderStatisticService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Validated
@Slf4j(topic = "ADMIN-CONTROLLER")
@RequiredArgsConstructor
public class AdminController {
  private final AdminUserService adminUserService;
  private final OrderStatisticService orderStatisticService;
  private final OrderService orderService;

  @GetMapping("/user")
  public BaseResponseList<UserResponseDTO> getAllUsers() {
    List<UserResponseDTO> users = adminUserService.getAllUsers();
    PageResponse.PageInfo pageInfo = PageResponse.PageInfo.builder()
        .currentPage(1)
        .totalItems(users.size()) // 👈 chính là size mà bố muốn
        .totalPages(1)
        .hasNextPage(false)
        .hasPreviousPage(false)
        .build();

    return new BaseResponseList<>(users, pageInfo);
  }

  @GetMapping("/order/statistic")
  public ResponseEntity<?> getOrderStatisticOfWebsite() {
    LocalDate today = LocalDate.now();
    LocalDate yesterday = LocalDate.now().minusDays(4);
    var res = orderStatisticService.getOverallOrderStatistic(yesterday, today);

    return ResponseEntity.ok().body(res);
  }
  @GetMapping("/order")
  public ResponseEntity<?> getOverallOrder(@RequestParam(required = false) OrderStatus orderStatus,
      @RequestParam(defaultValue = "0", required = false) int pageNo,
      @RequestParam(required = true) int pageSize,
      @RequestParam(required = false, defaultValue = "null ") String sortBy) {
    PageResponse<?> res = orderService.getAllOrderOfSystem(pageNo, pageSize, sortBy, orderStatus);

    return ResponseEntity.ok().body(res);

  }
}
