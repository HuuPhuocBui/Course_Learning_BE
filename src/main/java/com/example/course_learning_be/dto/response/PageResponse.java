package com.example.course_learning_be.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageResponse<T> {
  private List<T> data;
  private PageInfo pageInfo;

  @Data
  @Builder
  public static class PageInfo {
    private int currentPage;
    private long totalItems;
    private int totalPages;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
  }
}
