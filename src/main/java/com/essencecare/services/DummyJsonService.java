package com.essencecare.services;

import com.essencecare.models.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

public class DummyJsonService {
    private static final String BASE_URL = "https://dummyjson.com";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public List<Product> getAllProducts() throws IOException {
        HttpGet request = new HttpGet(BASE_URL + "/products");
        String response = EntityUtils.toString(httpClient.execute(request).getEntity());
        
        // The DummyJSON API returns products in a "products" array
        JsonNode root = objectMapper.readTree(response);
        return objectMapper.convertValue(root.get("products"), 
            objectMapper.getTypeFactory().constructCollectionType(List.class, Product.class));
    }

    public Product getProduct(Long id) throws IOException {
        HttpGet request = new HttpGet(BASE_URL + "/products/" + id);
        String response = EntityUtils.toString(httpClient.execute(request).getEntity());
        return objectMapper.readValue(response, Product.class);
    }
}
