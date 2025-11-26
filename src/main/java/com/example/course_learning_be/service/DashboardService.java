package com.example.course_learning_be.service;

import com.example.course_learning_be.dto.response.BaseResponseList;
import com.example.course_learning_be.dto.response.DashboardFifthResponseDTO;
import com.example.course_learning_be.dto.response.DashboardFirstResponseDTO;
import com.example.course_learning_be.dto.response.OrderGetResponseDTO;
import com.example.course_learning_be.dto.response.PageResponse;
import com.example.course_learning_be.entity.Order;
import com.example.course_learning_be.entity.Purchase;
import com.example.course_learning_be.enums.OrderStatus;
import com.example.course_learning_be.repository.OrderRepository;
import com.example.course_learning_be.repository.PurchaseRepository;
import com.example.course_learning_be.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {
   private final UserRepository userRepository;
   private final OrderRepository orderRepository;
   private final PurchaseRepository purchaseRepository;
   public DashboardFirstResponseDTO getFirstPart(){
      long totalUsers = userRepository.count();

      long totalCompletedOrders = orderRepository.countByStatus(OrderStatus.COMPLETE);

      return DashboardFirstResponseDTO.builder()
          .totalCustomer(totalUsers)
          .totalCompletedOrder(totalCompletedOrders)
          .build();
   }
   public BaseResponseList<DashboardFifthResponseDTO> getFifthPart(){
      List<Purchase> purchases = purchaseRepository.findAll();

      // Group theo courseId để đếm số mua
      Map<String, List<Purchase>> grouped = purchases.stream()
          .collect(Collectors.groupingBy(Purchase::getCourseId));

      // Chuyển sang DTO
      List<DashboardFifthResponseDTO> data = grouped.entrySet().stream().map(entry -> {
         Purchase first = entry.getValue().get(0);
         return DashboardFifthResponseDTO.builder()
             .title(first.getCourseName())
             .authorName(first.getBuyerName()) // giả sử authorName là sellerId
             .totalPurchase(entry.getValue().size())
             .build();
      })
          .sorted((a, b) -> Long.compare(b.getTotalPurchase(), a.getTotalPurchase()))
          .limit(10)
          .toList();

      // Tạo PageInfo tạm (FE dùng data thôi)
      PageResponse.PageInfo pageInfo = PageResponse.PageInfo.builder()
          .currentPage(1)
          .totalItems(data.size())
          .totalPages(1)
          .hasNextPage(false)
          .hasPreviousPage(false)
          .build();

      // Trả về BaseResponseList
      return BaseResponseList.<DashboardFifthResponseDTO>builder()
          .data(data)
          .pageInfo(pageInfo)
          .build();
   }
   public BaseResponseList<OrderGetResponseDTO> getSixthPart(){
      List<Order> orders = orderRepository.findTop10ByOrderByCreatedAtDesc();

      // 2. Map Order sang OrderGetResponseDTO
      List<OrderGetResponseDTO> orderDTOs = orders.stream()
          .map(order -> OrderGetResponseDTO.builder()
              .id(order.getId())
              .buyerEmail(userRepository.findById(order.getUserId()).orElseThrow(() -> new RuntimeException("User not found")).getEmail()) // giả sử userId là email, nếu không thì cần fetch từ User
              .courseNames(order.getCourseIds()) // nếu muốn tên khóa học thật, cần join với CourseRepository
              .status(order.getStatus())
              .paymentMethod(order.getPaymentMethod())
              .paidAt(order.getPaidAt())
              .totalAmount(order.getTotalAmount())
              .build())
          .limit(10)
          .collect(Collectors.toList());

      // 3. Trả về BaseResponseList với pageInfo null (hoặc bạn có thể set PageInfo)
      return BaseResponseList.<OrderGetResponseDTO>builder()
          .data(orderDTOs)
          .pageInfo(null) // nếu muốn paging thì set PageInfo ở đây
          .build();
   }
}
