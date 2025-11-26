package com.example.course_learning_be.service;

import com.example.course_learning_be.dto.response.OrderStatisticResponseDTO;
import com.example.course_learning_be.entity.Order;
import com.example.course_learning_be.repository.OrderRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatisticServiceImpl implements OrderStatisticService{
  private final MongoTemplate mongoTemplate;
  private final OrderRepository orderRepository;
  @Override
  public OrderStatisticResponseDTO getOverallOrderStatistic(LocalDate startDate, LocalDate endDate) {


    List<DailyOrderCount> dailyOrderCounts = countOrdersPerDay(startDate, endDate);

    if (dailyOrderCounts.isEmpty()) {
      return OrderStatisticResponseDTO.builder()
          .startDate(startDate.toString())
          .endDate(endDate.toString())
          .totalOrder(0)
          .dataOverTime(countOrdersPerDay(startDate, endDate))
          .deltaInPercentage(0)
          .build();
    }
    long totalOrder = dailyOrderCounts.stream().mapToLong(DailyOrderCount::getCount).sum();
    long orderNumAtFirst = dailyOrderCounts.getFirst().getCount();
    double overallDelta = ((double) (totalOrder - orderNumAtFirst) /orderNumAtFirst) * 100;


    return OrderStatisticResponseDTO.builder()
        .startDate(startDate.toString())
        .endDate(endDate.toString())
        .totalOrder(totalOrder)
        .dataOverTime(countOrdersPerDay(startDate, endDate))
        .deltaInPercentage(overallDelta)
        .build();

  }


  public List<DailyOrderCount> countOrdersPerDay(LocalDate fromDate, LocalDate toDate) {
    ZoneId zone = ZoneId.of("UTC");

    Date from = Date.from(fromDate.atStartOfDay(zone).toInstant());
    Date to = Date.from(toDate.plusDays(1).atStartOfDay(zone).toInstant());

    MatchOperation match = Aggregation.match(
        Criteria.where("paid_at").gte(from).lt(to)
    );

    ProjectionOperation projectDate = Aggregation.project()
        .andExpression("{ $dateToString: { format: '%Y-%m-%d', date: '$paid_at', timezone: 'UTC' } }")
        .as("dateString");
    GroupOperation group = Aggregation.group("dateString").count().as("count");

    SortOperation sort = Aggregation.sort(Sort.by(Sort.Direction.ASC, "_id"));

    Aggregation aggregation = Aggregation.newAggregation(
        match,
        projectDate,
        group,
        sort
    );

    AggregationResults<Document> results =
        mongoTemplate.aggregate(aggregation, "orders", Document.class);

    return results.getMappedResults().stream()
        .map(doc -> DailyOrderCount.builder()
            .date(doc.getString("_id"))
            .count(doc.getInteger("count"))
            .build())
        .collect(Collectors.toList());
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DailyOrderCount {
    private Object date; // You can parse this into LocalDate if needed
    private int count;
  }


  private List<Order> getOrdersPaidBetween(LocalDate fromDate, LocalDate toDate) {
    Instant from = fromDate.atStartOfDay(ZoneId.of("UTC")).toInstant();
    Instant to = toDate.plusDays(1).atStartOfDay(ZoneId.of("UTC")).toInstant();
    return orderRepository.findByPaidAtBetween(from, to);
  }

  private List<Order> getOrdersRaisedBetween(LocalDate fromDate, LocalDate toDate) {
    Instant from = fromDate.atStartOfDay(ZoneId.of("UTC")).toInstant();
    Instant to = toDate.plusDays(1).atStartOfDay(ZoneId.of("UTC")).toInstant();
    return orderRepository.findByRaisedAtBetween(from, to);
  }
}

