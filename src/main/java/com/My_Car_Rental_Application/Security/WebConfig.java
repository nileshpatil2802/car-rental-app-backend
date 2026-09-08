package com.My_Car_Rental_Application.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig
        implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry) {

        registry
                .addResourceHandler(
                        "/car-images/**"
                )
                .addResourceLocations(
                        "file:///D:/CarRentalUploads/cars/"
                );
    }
}
