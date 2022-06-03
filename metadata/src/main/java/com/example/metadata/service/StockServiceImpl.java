package com.example.metadata.service;

import com.example.metadata.entity.Stock;
import com.example.metadata.model.StockModel;
import com.example.metadata.repository.StockRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

  private final StockRepository repository;

  @Override
  public Stock createStock(StockModel model) {
    log.info("Enter createStock: stock: {}", model);
    final Stock stock =
        Stock.builder()
            .stockName(model.getName())
            .stockTicker(model.getTicker())
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();
    final Stock stockEntity = repository.save(stock);
    log.info("stockEntity: {}", stockEntity);
    return stockEntity;
  }

  @Override
  public Stock getStock(long id) {
    log.info("Enter getStock: id: {}", id);
    return repository
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found"));
  }

  @Override
  public List<Stock> getAllStocks() {
    return repository.findAll();
  }
}
