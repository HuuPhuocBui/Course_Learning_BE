package com.example.course_learning_be.controller;

import com.example.course_learning_be.dto.request.AddToCartRequestDTO;
import com.example.course_learning_be.service.CartService;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@Validated
@Slf4j(topic = "CART-CONTROLLER")
@RequiredArgsConstructor
public class CartController {
  private final CartService cartService;
  @PostMapping("/item")
  public ResponseEntity<?> addToCart(@RequestBody AddToCartRequestDTO requestDTO) {
    cartService.addToCart(requestDTO);

    return ResponseEntity.ok().body(null);

  }
  @DeleteMapping("/item/{courseId}")
  public ResponseEntity<?> deleteFromCart(@PathVariable String courseId) {
    cartService.deleteItem(courseId);

    return ResponseEntity.ok().body(null);
  }
  @PostMapping("/purchase")
  public ResponseEntity<?> buyAllFromCart() throws UnsupportedEncodingException {
    String paymentUrl = cartService.purchaseAll();

    return ResponseEntity.ok().body(paymentUrl);
  }
}
