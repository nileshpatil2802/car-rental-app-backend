package com.My_Car_Rental_Application.dto;

import com.My_Car_Rental_Application.entity.Features;

public class CarsDataRequestDto {

    private int userId;
    private Long carId;

    // Base64 image data
    private String mainImage;
    private String img1;
    private String img2;
    private String img3;

    // Original image filenames
    private String mainImageName;
    private String img1Name;
    private String img2Name;
    private String img3Name;

    private boolean status;
    private String fuelType;
    private String name;
    private int seating;
    private String transmition;
    private double price;
    private Features features;
    private String description;
    private String brand;

    public CarsDataRequestDto() {
    }

    public CarsDataRequestDto(
            int userId,
            Long carId,
            String mainImage,
            String img1,
            String img2,
            String img3,
            String mainImageName,
            String img1Name,
            String img2Name,
            String img3Name,
            boolean status,
            String fuelType,
            String name,
            int seating,
            String transmition,
            double price,
            Features features,
            String description,
            String brand) {

        this.userId = userId;
        this.carId = carId;
        this.mainImage = mainImage;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.mainImageName = mainImageName;
        this.img1Name = img1Name;
        this.img2Name = img2Name;
        this.img3Name = img3Name;
        this.status = status;
        this.fuelType = fuelType;
        this.name = name;
        this.seating = seating;
        this.transmition = transmition;
        this.price = price;
        this.features = features;
        this.description = description;
        this.brand = brand;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getMainImageName() {
        return mainImageName;
    }

    public void setMainImageName(String mainImageName) {
        this.mainImageName = mainImageName;
    }

    public String getImg1Name() {
        return img1Name;
    }

    public void setImg1Name(String img1Name) {
        this.img1Name = img1Name;
    }

    public String getImg2Name() {
        return img2Name;
    }

    public void setImg2Name(String img2Name) {
        this.img2Name = img2Name;
    }

    public String getImg3Name() {
        return img3Name;
    }

    public void setImg3Name(String img3Name) {
        this.img3Name = img3Name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeating() {
        return seating;
    }

    public void setSeating(int seating) {
        this.seating = seating;
    }

    public String getTransmition() {
        return transmition;
    }

    public void setTransmition(String transmition) {
        this.transmition = transmition;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Features getFeatures() {
        return features;
    }

    public void setFeatures(Features features) {
        this.features = features;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}