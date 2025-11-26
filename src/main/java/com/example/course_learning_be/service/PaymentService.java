package com.example.course_learning_be.service;

import com.example.course_learning_be.Util.SecurityUtil;
import com.example.course_learning_be.dto.request.OrderPaymentDTO;
import com.example.course_learning_be.dto.response.PaymentBillResponseDTO;
import com.example.course_learning_be.dto.response.VnpayResponseDTO;
import com.example.course_learning_be.entity.Course;
import com.example.course_learning_be.entity.CourseStudent;
import com.example.course_learning_be.entity.Order;
import com.example.course_learning_be.entity.Payment;
import com.example.course_learning_be.entity.Purchase;
import com.example.course_learning_be.entity.User;
import com.example.course_learning_be.enums.OrderStatus;
import com.example.course_learning_be.enums.PaymentStatus;
import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import com.example.course_learning_be.repository.CartRepository;
import com.example.course_learning_be.repository.CourseRepository;
import com.example.course_learning_be.repository.CourseStudentRepository;
import com.example.course_learning_be.repository.OrderRepository;
import com.example.course_learning_be.repository.PaymentRepository;
import com.example.course_learning_be.repository.PurchaseRepository;
import com.example.course_learning_be.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
 private final OrderRepository orderRepository;
 private final UserRepository userRepository;
 private final CourseStudentRepository courseStudentRepository;
 private final CourseRepository courseRepository;
 private final PurchaseRepository purchaseRepository;
 private final PaymentRepository paymentRepository;
 private final CartRepository cartRepository;
 private final SecurityUtil securityUtil;

  public String createPayment(OrderPaymentDTO orderPaymentDTO) throws UnsupportedEncodingException {
    return generatePaymentUrl(orderPaymentDTO.getTotalAmount(), orderPaymentDTO.getOrderId());
  }

  public String generatePaymentUrl(long totalAmount, String vnp_TxnRef) throws UnsupportedEncodingException {
    String vnp_Version = "2.1.0";
    String vnp_Command = "pay";
    String orderType = "other";

    long amount = totalAmount;
    log.info("Amount: " + amount);

    String bankCode = "NCB";
    String vnp_IpAddr = "127.0.0.1";
    String vnp_TmnCode = VnpayConfigurer.vnp_TmnCode;

    Map<String, String> vnp_Params = new HashMap<>();
    vnp_Params.put("vnp_Version", vnp_Version);
    vnp_Params.put("vnp_Command", vnp_Command);
    vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
    vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
    vnp_Params.put("vnp_CurrCode", "VND");
    vnp_Params.put("vnp_BankCode", bankCode);
    vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
    vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
    vnp_Params.put("vnp_OrderType", orderType);
    vnp_Params.put("vnp_Locale", "vn");
    vnp_Params.put("vnp_ReturnUrl", VnpayConfigurer.vnp_ReturnUrl);
    vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String vnp_CreateDate = formatter.format(cld.getTime());
    vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

    cld.add(Calendar.MINUTE, 15);
    String vnp_ExpireDate = formatter.format(cld.getTime());
    vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

    // Sort keys
    List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
    Collections.sort(fieldNames);

    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();

    for (String fieldName : fieldNames) {
      String fieldValue = vnp_Params.get(fieldName);
      if (fieldValue != null && !fieldValue.isEmpty()) {
        // HashData dùng US-ASCII (dùng cho secure hash)
        hashData.append(fieldName).append("=").append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString())).append("&");

        // Query dùng UTF-8 (dùng cho URL)
        query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
            .append("=")
            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()))
            .append("&");
      }
    }

    // Remove last &
    if (hashData.length() > 0) hashData.setLength(hashData.length() - 1);
    if (query.length() > 0) query.setLength(query.length() - 1);

    // Sinh chữ ký
    String vnp_SecureHash = VnpayConfigurer.hmacSHA512(VnpayConfigurer.secretKey, hashData.toString());
    query.append("&vnp_SecureHash=").append(vnp_SecureHash);

    return VnpayConfigurer.vnp_PayUrl + "?" + query.toString();
  }

  public void checkSecureHash(HttpServletRequest request) {
    String vnpSecureHash = request.getParameter("vnp_SecureHash");

    Map<String, String> fields = new HashMap<>();
    request.getParameterMap().forEach((key, value) -> {
      if (key.startsWith("vnp_") && !key.equals("vnp_SecureHash") && !key.equals("vnp_SecureHashType")) {
        fields.put(key, value[0]);
      }
    });

    String signData = VnpayHelper.buildData(fields);
    String calculatedHash = VnpayHelper.hmacSHA512(VnpayConfigurer.secretKey, signData);

    if (!calculatedHash.equalsIgnoreCase(vnpSecureHash)) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }
  }

    public int handlePaymentResponse(VnpayResponseDTO vnpayResponseDTO) throws UnsupportedEncodingException {
      if (vnpayResponseDTO.getResponseCode().equals("00")) {
        String orderId = vnpayResponseDTO.getVnpTxnRef();
        String transactionId = vnpayResponseDTO.getVnpTransactionNo();
        String paymentMethod = vnpayResponseDTO.getVnpCardType();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
        OrderService.addOtherInfoCaseSuccessful(order, transactionId, paymentMethod);
        orderRepository.save(order);
        clearCart(order.getUserId());
        addUserToCourse(order.getUserId(), order.getCourseIds());

        savePayment(vnpayResponseDTO, order.getUserId());

        savePurchase(order);

        return 0;
      } else {
        Order order = orderRepository.findById(vnpayResponseDTO.getVnpTxnRef()).orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return -1;
      }
    }

  public void clearCart(String userId) {
    cartRepository.deleteAllByUserId(userId);
  }


  public void addUserToCourse(String userId, List<String> courseIds) {
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Course not found"));

    List<CourseStudent> opt = new ArrayList<>();

    for (String courseId : courseIds) {
      CourseStudent courseStudent = getCourseStudentById(courseId);
      courseStudent.setCourseId(courseId);
      courseStudent.addStudent(user.getId());
      opt.add(courseStudent);
    }

    //for db optimizing
    courseStudentRepository.saveAll(opt);

    user.getCourses().addAll(courseIds);
    userRepository.save(user);

  }

  public void savePurchase(Order order) {
    List<Course> courses = courseRepository.findAllById(order.getCourseIds());
    User buyer = userRepository.findById(order.getUserId()).orElseThrow(() -> new RuntimeException("Course not found"));
    List<Purchase> purchases = new ArrayList<>();
    for (Course course : courses) {
      Purchase purchase = Purchase.builder()
          .buyerId(order.getUserId())
          .buyerName(buyer.getFullName())
          .buyerEmail(buyer.getEmail())
          .sellerId(course.getOwner().getId())
          .profit(course.getPrice())
          .courseId(course.getId())
          .courseName(course.getTitle())
          .buyDate(order.getPaidAt().toString())
          .build();
      purchases.add(purchase);
    }
    purchaseRepository.saveAll(purchases);
  }

  public void savePayment(VnpayResponseDTO response, String buyerId) {
    Payment payment = Payment.builder()
        .merchantId(buyerId)
        .amount(response.getVnpAmount())
        .bank(response.getVnpBankCode())
        .orderInfo(response.getVnpOrderInfo())
        .orderId(response.getVnpTxnRef())
        .transactionId(response.getVnpTransactionNo())
        .cardType(response.getVnpCardType())
        .currency(response.getVnpCurrency())
        .payDate(response.getVnpPayDate())
        .status(PaymentStatus.SUCCESS)
        .build();

    paymentRepository.save(payment);

  }
  public CourseStudent getCourseStudentById(String id) {
    return courseStudentRepository.findById(id).orElse(new CourseStudent());
  }

  public PaymentBillResponseDTO getBill(String transactionId) {
    User user = securityUtil.getCurrentUser();

    Payment payment = paymentRepository.findByTransactionId(transactionId);
    return PaymentBillResponseDTO.builder()
        .merchantEmail(user.getEmail())
        .amount(payment.getAmount())
        .bank(payment.getBank())
        .transactionId(transactionId)
        .cardType(payment.getCardType())
        .currency(payment.getCurrency())
        .payDate(payment.getPayDate())
        .status(payment.getStatus())
        .orderInfo(payment.getOrderInfo())
        .build();
  }
}
