package com.example.consumer.redis;

import com.example.consumer.model.ProductData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
class RedisService implements PersistenceService {

    private final ProductRepository repository;

    public void saveProducts(List<ProductData> itemsList) {
        log.warn("Saving items size {}", itemsList.size());
        repository.saveAll(itemsList);
    }

    public List<ProductData> getProducts() {
        List<ProductData> items = new ArrayList<>();
        repository.findAll().forEach(items::add);
        return items;
    }
}
