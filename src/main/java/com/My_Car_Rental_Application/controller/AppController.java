package com.My_Car_Rental_Application.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @GetMapping("/")
    public String index() {
        return "Car Rental Backend API is running successfully!";
    }
}
