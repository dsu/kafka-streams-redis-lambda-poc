package com.example.producer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductData {

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
