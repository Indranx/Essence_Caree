package com.essencecare.repository;

import com.essencecare.models.User;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final Map<String, User> users = new HashMap<>();

    static {
        // Initialize with some hardcoded users
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("john");
        user1.setPassword("password123"); // In real app, this should be hashed
        user1.setEmail("john@example.com");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        users.put(user1.getUsername(), user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("alice");
        user2.setPassword("password456");
        user2.setEmail("alice@example.com");
        user2.setFirstName("Alice");
        user2.setLastName("Smith");
        users.put(user2.getUsername(), user2);
    }

    public static User findByUsername(String username) {
        return users.get(username);
    }

    public static boolean validateCredentials(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }
} 