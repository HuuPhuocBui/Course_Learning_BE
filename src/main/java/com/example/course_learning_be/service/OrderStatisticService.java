package com.example.course_learning_be.service;

import com.example.course_learning_be.dto.response.OrderStatisticResponseDTO;
import java.time.LocalDate;

public interface OrderStatisticService {
  OrderStatisticResponseDTO getOverallOrderStatistic(LocalDate startDate, LocalDate endDate);
}
