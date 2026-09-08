package com.My_Car_Rental_Application.dto;



public class AddToCartDto {

    private Integer userId;
    private Integer carId;

    public AddToCartDto() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }
}
