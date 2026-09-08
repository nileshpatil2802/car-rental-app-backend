package com.My_Car_Rental_Application.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    public void createNotification(Integer userId, String title, String message, String type, Integer referenceId) {
        System.out.println("📨 Notification created:");
        System.out.println("User ID: " + userId);
        System.out.println("Title: " + title);
        System.out.println("Message: " + message);
        System.out.println("Type: " + type);
        System.out.println("Reference ID: " + referenceId);
        System.out.println("Time: " + LocalDateTime.now());
    }
}