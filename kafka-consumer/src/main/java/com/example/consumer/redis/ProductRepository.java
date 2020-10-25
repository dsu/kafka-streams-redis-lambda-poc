package com.example.consumer.redis;

import com.example.consumer.model.ProductData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<ProductData, String> {

}
