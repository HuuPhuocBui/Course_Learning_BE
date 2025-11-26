package com.example.course_learning_be.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
  private String cartId;
  private String userId;
  private List<CartItemResponseDTO> cartItems;
  private int productCount;
  private double totalPrice;
}
