package com.example.course_learning_be.dto.request;

import com.example.course_learning_be.enums.Language;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
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
public class BenefitRequestDTO {
  @NotEmpty(message = "Must include at least one language for title")
  private Map<Language, String> title;

  @NotEmpty(message = "Must include at least one language for description")
  private Map<Language, String> description;
}
