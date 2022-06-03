package com.example.metadata.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StockModel {
  @EqualsAndHashCode.Include private Long id;

  @NotEmpty(message = "name is required")
  @Size(
      min = 3,
      max = 50,
      message = "Stock name '${validatedValue}' must be between {min} and {max} characters long")
  private String name;

  @NotEmpty(message = "ticker is required")
  @Size(
      min = 3,
      max = 50,
      message = "Stock ticker '${validatedValue}' must be between {min} and {max} characters long")
  private String ticker;
}
