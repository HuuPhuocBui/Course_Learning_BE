package com.example.course_learning_be.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VnpayHelper {
  public static String hmacSHA512(String key, String data) {
    try {
      Mac hmac512 = Mac.getInstance("HmacSHA512");
      SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
      hmac512.init(secretKeySpec);
      byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
      StringBuilder hash = new StringBuilder();
      for (byte b : bytes) {
        hash.append(String.format("%02x", b));
      }
      return hash.toString();
    } catch (Exception ex) {
      throw new RuntimeException("Error while hashing: " + ex.getMessage(), ex);
    }
  }

  public static String buildData(Map<String, String> fields) {
    // Sắp xếp các tham số theo thứ tự alphabet
    List<String> keys = new ArrayList<>(fields.keySet());
    Collections.sort(keys);

    StringBuilder query = new StringBuilder();
    for (String key : keys) {
      if (fields.get(key) != null && fields.get(key).length() > 0) {
        query.append(URLEncoder.encode(key, StandardCharsets.US_ASCII));
        query.append('=');
        query.append(URLEncoder.encode(fields.get(key), StandardCharsets.US_ASCII));
        query.append('&');
      }
    }
    // Xóa dấu `&` cuối
    if (query.length() > 0) query.setLength(query.length() - 1);
    return query.toString();
  }
}
