package com.example.consumer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@NoArgsConstructor
@Builder
@JsonInclude
@AllArgsConstructor

@Getter
@ToString
@RedisHash("Product")
public class ProductData {

    @Id
    private int productId;
    private String productName;
    private String shortDescription;
    private String description;
    private String price;
    private String primaryAsset;
    private String size;
    private String gender;
    private String color;

}
