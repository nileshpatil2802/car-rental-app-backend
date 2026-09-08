package com.My_Car_Rental_Application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.My_Car_Rental_Application.entity.Features;

public class BookingResponseDto {

	private Integer bookingId;
   
	private Integer userId;
	private String userName;
	private String firstName;
	private String lastName;
    private String userEmail;
    
    
    private Integer carId;
    private String mainImage;
    private String img1;
    private String img2;
    private String img3;

    private String status;

    private String fuelType;
    private String name;
    private Integer seating;
    private String transmition;
    private Double price;

    private Features features;

    private String description;
    private String brand;

    private LocalDateTime createdAt;
    
    private LocalDate pickupDate;

    private LocalDate dropoffDate;
    private String pickupLocation;
    private String dropoffLocation;
    private String tripDriverType;
    private Double total;
    private int days;
   
	public BookingResponseDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BookingResponseDto(Integer bookingId, Integer userId, String userName, String firstName, String lastName,
			String userEmail, Integer carId, String mainImage, String img1, String img2, String img3, String status,
			String fuelType, String name, Integer seating, String transmition, Double price, Features features,
			String description, String brand, LocalDateTime createdAt, LocalDate pickupDate, LocalDate dropoffDate,
			String pickupLocation, String dropoffLocation, String tripDriverType, Double total, int days) {
		super();
		this.bookingId = bookingId;
		this.userId = userId;
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userEmail = userEmail;
		this.carId = carId;
		this.mainImage = mainImage;
		this.img1 = img1;
		this.img2 = img2;
		this.img3 = img3;
		this.status = status;
		this.fuelType = fuelType;
		this.name = name;
		this.seating = seating;
		this.transmition = transmition;
		this.price = price;
		this.features = features;
		this.description = description;
		this.brand = brand;
		this.createdAt = createdAt;
		this.pickupDate = pickupDate;
		this.dropoffDate = dropoffDate;
		this.pickupLocation = pickupLocation;
		this.dropoffLocation = dropoffLocation;
		this.tripDriverType = tripDriverType;
		this.total = total;
		this.days = days;
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Integer getCarId() {
		return carId;
	}

	public void setCarId(Integer carId) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
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

	public Integer getSeating() {
		return seating;
	}

	public void setSeating(Integer seating) {
		this.seating = seating;
	}

	public String getTransmition() {
		return transmition;
	}

	public void setTransmition(String transmition) {
		this.transmition = transmition;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDate getPickupDate() {
		return pickupDate;
	}

	public void setPickupDate(LocalDate pickupDate) {
		this.pickupDate = pickupDate;
	}

	public LocalDate getDropoffDate() {
		return dropoffDate;
	}

	public void setDropoffDate(LocalDate dropoffDate) {
		this.dropoffDate = dropoffDate;
	}

	public String getPickupLocation() {
		return pickupLocation;
	}

	public void setPickupLocation(String pickupLocation) {
		this.pickupLocation = pickupLocation;
	}

	public String getDropoffLocation() {
		return dropoffLocation;
	}

	public void setDropoffLocation(String dropoffLocation) {
		this.dropoffLocation = dropoffLocation;
	}

	public String getTripDriverType() {
		return tripDriverType;
	}

	public void setTripDriverType(String tripDriverType) {
		this.tripDriverType = tripDriverType;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	
    
    
}
