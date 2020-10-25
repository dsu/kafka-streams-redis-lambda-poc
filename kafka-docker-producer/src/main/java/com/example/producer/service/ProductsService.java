package com.example.producer.service;

import com.example.producer.model.ProductData;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductsService {

    protected static final Logger logger = LoggerFactory.getLogger(ProductsService.class);

    private final KafkaSender sender;


    public void importCSV() throws IOException, CsvValidationException {
        List<List<String>> records = new ArrayList<>();

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("MOCK_DATA.csv");
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(is));) {
            addProducts(records, csvReader);
        }
        sendProducts(records);
    }

    private void sendProducts(List<List<String>> records) {
        for (List<String> item : records) {
            ProductData data = buildProduct(item);
            sendToKafka(data);
        }
    }

    private void addProducts(List<List<String>> records, CSVReader csvReader) throws IOException, CsvValidationException {
        String[] values = null;
        int index = 0;
        while ((values = csvReader.readNext()) != null) {
            if (index++ < 1) {
                //skip the header
                continue;
            }
            records.add(Arrays.asList(values));
        }
    }

    private ProductData buildProduct(List<String> item) {
        int index = 0;
        return ProductData
                .builder()
                .productId(Integer.parseInt(item.get(index++).trim()))
                .productName(item.get(index++).trim())
                .shortDescription(item.get(index++).trim())
                .description(item.get(index++).trim())
                .price(item.get(index++).trim())
                .primaryAsset(item.get(index++).trim())
                .size(item.get(index++).trim())
                .gender(item.get(index++).trim())
                .color(item.get(index++).trim())
                .build();
    }

    private void sendToKafka(ProductData sub) {
        logger.info("Sending a message to Kafka");
        sender.send(sub,
                msg -> logger.info("Send to the kafka, msg : {} ", msg),
                () -> logger.error("Send to the kafka failed"));
    }

}
