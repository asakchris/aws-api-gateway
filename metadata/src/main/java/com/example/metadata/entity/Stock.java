package com.example.metadata.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

@Entity(name = "t_stock")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  @Column(name = "stock_id")
  private Long stockId;

  @Column(name = "stock_name")
  private String stockName;

  @Column(name = "stock_ticker")
  private String stockTicker;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "modified_at")
  private LocalDateTime modifiedAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Stock stock = (Stock) o;
    return stockId != null && Objects.equals(stockId, stock.stockId);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
