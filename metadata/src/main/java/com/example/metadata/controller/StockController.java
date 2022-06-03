package com.example.metadata.controller;

import com.example.metadata.entity.Stock;
import com.example.metadata.model.StockModel;
import com.example.metadata.service.StockService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StockController {
  private final StockService service;

  @PostMapping(value = "/stocks")
  @ResponseStatus(code = HttpStatus.CREATED)
  public Stock createStock(@Valid @RequestBody StockModel model) {
    log.info("Enter createStock: stock: {}", model);
    final Stock stock = service.createStock(model);
    log.info("stock: {}", stock);
    return stock;
  }

  @GetMapping(value = "/stocks/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  public Stock getStock(@PathVariable long id) {
    log.info("Enter getStock: id: {}", id);
    return service.getStock(id);
  }

  @GetMapping(value = "/stocks")
  @ResponseStatus(code = HttpStatus.OK)
  public List<Stock> getAllStocks() {
    log.info("Enter getAllStocks");
    return service.getAllStocks();
  }
}
