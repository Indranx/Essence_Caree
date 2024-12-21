package com.essencecare.controllers;

import com.essencecare.services.DummyJsonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/products/*")
public class ProductServlet extends HttpServlet {
    private final DummyJsonService dummyJsonService = new DummyJsonService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all products
                String json = objectMapper.writeValueAsString(dummyJsonService.getAllProducts());
                response.getWriter().write(json);
            } else {
                // Get single product
                Long productId = Long.parseLong(pathInfo.substring(1));
                String json = objectMapper.writeValueAsString(dummyJsonService.getProduct(productId));
                response.getWriter().write(json);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
} 