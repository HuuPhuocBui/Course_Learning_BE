package com.example.course_learning_be.dto.response;

import com.example.course_learning_be.dto.response.PageResponse.PageInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BaseResponseList<T> {
  private List<T> data;
  private PageInfo pageInfo;
}
