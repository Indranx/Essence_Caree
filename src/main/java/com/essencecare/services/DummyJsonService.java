package com.essencecare.services;

import com.essencecare.models.AuthRequest;
import com.essencecare.models.User;
import com.essencecare.models.Product;
import com.essencecare.models.ErrorResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

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

    public User login(AuthRequest authRequest) throws IOException {
        System.out.println("Starting login attempt for user: " + authRequest.getUsername());
        
        HttpPost request = new HttpPost(BASE_URL + "/auth/login");
        request.setHeader("Content-Type", "application/json");
        
        // Create JSON object using ObjectMapper instead of String formatting
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("username", authRequest.getUsername());
        jsonNode.put("password", authRequest.getPassword());
        String jsonRequest = objectMapper.writeValueAsString(jsonNode);
        
        System.out.println("Request JSON: " + jsonRequest);
        
        StringEntity entity = new StringEntity(jsonRequest, StandardCharsets.UTF_8);
        request.setEntity(entity);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpResponse httpResponse = client.execute(request);
            String responseBody = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            
            System.out.println("DummyJSON Response Status: " + statusCode);
            System.out.println("DummyJSON Response Body: " + responseBody);
            
            if (statusCode != 200) {
                ErrorResponse error = objectMapper.readValue(responseBody, ErrorResponse.class);
                throw new RuntimeException(error.getMessage());
            }

            return objectMapper.readValue(responseBody, User.class);
        }
    }

    public User getCurrentUser(String token) throws IOException {
        HttpGet request = new HttpGet(BASE_URL + "/auth/me");
        request.setHeader("Authorization", "Bearer " + token);
        
        String response = EntityUtils.toString(httpClient.execute(request).getEntity());
        return objectMapper.readValue(response, User.class);
    }

    // Add this method to test direct API access
    public void testDirectApiAccess() throws IOException {
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("username", "kminchelle");
        jsonNode.put("password", "0lelplR");
        String jsonRequest = objectMapper.writeValueAsString(jsonNode);
        
        System.out.println("Starting direct API test...");
        System.out.println("Request JSON: " + jsonRequest);
        
        HttpPost request = new HttpPost(BASE_URL + "/auth/login");
        request.setHeader("Content-Type", "application/json");
        
        StringEntity entity = new StringEntity(jsonRequest, StandardCharsets.UTF_8);
        request.setEntity(entity);
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpResponse response = client.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            
            System.out.println("Status Code: " + response.getStatusLine().getStatusCode());
            System.out.println("Response Headers: ");
            Arrays.stream(response.getAllHeaders()).forEach(h -> 
                System.out.println(h.getName() + ": " + h.getValue()));
            System.out.println("Response Body: " + responseBody);
        }
    }
}
