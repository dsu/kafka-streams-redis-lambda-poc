package com.example.consumer.redis;

import com.example.consumer.model.ProductData;

import java.util.List;

public interface PersistenceService {

    void saveProducts(List<ProductData> metricsList);

    List<ProductData> getProducts();
}
