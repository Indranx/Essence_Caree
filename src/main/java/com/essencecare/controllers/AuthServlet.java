package com.essencecare.controllers;

import com.essencecare.models.AuthRequest;
import com.essencecare.models.User;
import com.essencecare.models.ErrorResponse;
import com.essencecare.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/login".equals(pathInfo)) {
                AuthRequest authRequest = objectMapper.readValue(
                    request.getReader(), 
                    AuthRequest.class
                );

                if (UserRepository.validateCredentials(authRequest.getUsername(), authRequest.getPassword())) {
                    User user = UserRepository.findByUsername(authRequest.getUsername());
                    
                    // Store user in session
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    
                    // Return user data
                    response.getWriter().write(objectMapper.writeValueAsString(user));
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    ErrorResponse errorResponse = new ErrorResponse("Invalid credentials");
                    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                }
            } else if ("/logout".equals(pathInfo)) {
                request.getSession().invalidate();
                response.getWriter().write(objectMapper.writeValueAsString(
                    new ErrorResponse("Logged out successfully")));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        
        try {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                response.getWriter().write(objectMapper.writeValueAsString(user));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(
                    objectMapper.writeValueAsString(new ErrorResponse("Not authenticated")));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                objectMapper.writeValueAsString(new ErrorResponse(e.getMessage())));
        }
    }
} 