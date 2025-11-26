package com.example.course_learning_be.dto.response;

import com.example.course_learning_be.service.OrderStatisticServiceImpl;
import com.example.course_learning_be.service.OrderStatisticServiceImpl.DailyOrderCount;
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
public class OrderStatisticResponseDTO {
  private String startDate;
  private String endDate;
  private long totalOrder;
  private List<DailyOrderCount> dataOverTime;
  private double deltaInPercentage;
}
