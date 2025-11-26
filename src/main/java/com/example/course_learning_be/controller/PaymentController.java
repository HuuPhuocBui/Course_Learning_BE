package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.request.OrderPaymentDTO;
import com.example.course_learning_be.dto.response.PaymentBillResponseDTO;
import com.example.course_learning_be.dto.response.VnpayResponseDTO;
import com.example.course_learning_be.service.OrderService;
import com.example.course_learning_be.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@Validated
@Slf4j(topic = "PAYMENT-CONTROLLER")
@RequiredArgsConstructor
public class PaymentController {
  private final PaymentService paymentService;
  private final OrderService orderService;
  @PostMapping("/create")
  public ResponseEntity<String> createPayment(@RequestBody OrderPaymentDTO orderPaymentDTO) throws UnsupportedEncodingException {
    log.info(orderPaymentDTO.toString());
    String res = paymentService.createPayment(orderPaymentDTO);
    return ResponseEntity.ok().body(res);
  }

  @GetMapping("/bill/{transactionId}")
  public ResponseEntity<?> getUserBill(@PathVariable(name = "transactionId") String transactionId) {
    PaymentBillResponseDTO res = paymentService.getBill(transactionId);

    return ResponseEntity.ok().body(res);
  }

  @GetMapping("/return")
  public void handle(@RequestParam("vnp_ResponseCode") String responseCode,
      @RequestParam("vnp_TxnRef") String vnp_TxnRef,
      @RequestParam("vnp_TransactionNo") String vnp_TransactionNo,
      @RequestParam("vnp_CardType") String vnp_CardType,
      @RequestParam("vnp_OrderInfo") String vnp_OrderInfo,
      @RequestParam("vnp_Amount") String vnp_Amount,
      @RequestParam(value = "vnp_Currency", required = false, defaultValue = "VND") String vnp_Currency,
      @RequestParam("vnp_BankCode") String vnp_BankCode,
      @RequestParam("vnp_PayDate") String vnp_PayDate,
      @RequestParam("vnp_SecureHash") String vnp_SecureHash,
      HttpServletRequest request,
      HttpServletResponse response) throws IOException {

    paymentService.checkSecureHash(request);
    final VnpayResponseDTO paymentResponse = VnpayResponseDTO.builder()
        .responseCode(responseCode)
        .vnpTxnRef(vnp_TxnRef)
        .vnpTransactionNo(vnp_TransactionNo)
        .vnpCardType(vnp_CardType)
        .vnpOrderInfo(vnp_OrderInfo)
        .vnpAmount(Long.toString(Long.parseLong(vnp_Amount) / 100))
        .vnpBankCode(vnp_BankCode)
        .vnpCurrency(vnp_Currency)
        .vnpPayDate(vnp_PayDate)
        .build();

    int status = paymentService.handlePaymentResponse(paymentResponse);

    if (status == -1) {
      response.sendRedirect("http://localhost:5173/payment/failure?transactionId=" + vnp_TransactionNo);
    } else {
      response.sendRedirect("http://localhost:5173/payment/success?transactionId=" + vnp_TransactionNo);
    }

  }

  @PostMapping("/course/{courseId}")
  public ResponseEntity<?> buyRightNow(@PathVariable("courseId") String courseId,
      @RequestParam(required = false) String timeZone) throws UnsupportedEncodingException {
    String paymentUrl = orderService.buyRightNow(courseId);
    return ResponseEntity.ok().body(paymentUrl);
  }

}
