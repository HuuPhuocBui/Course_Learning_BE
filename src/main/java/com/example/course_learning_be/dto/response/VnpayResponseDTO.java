package com.example.course_learning_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VnpayResponseDTO {
  private String responseCode;
  private String vnpTxnRef;
  private String vnpTransactionNo;
  private String vnpCardType;
  private String vnpOrderInfo;
  private String vnpAmount;
  private String vnpCurrency;
  private String vnpBankCode;
  private String vnpPayDate;

}
