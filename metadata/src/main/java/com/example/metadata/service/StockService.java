package com.example.metadata.service;

import com.example.metadata.entity.Stock;
import com.example.metadata.model.StockModel;
import java.util.List;

public interface StockService {
  Stock createStock(StockModel model);

  Stock getStock(long id);

  List<Stock> getAllStocks();
}
