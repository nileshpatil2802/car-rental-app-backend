package com.My_Car_Rental_Application.dto;

import java.time.LocalDate;

public class BookingDto {

    private Integer userId;

    private Integer carId;
    
    private Integer days;
    
    private LocalDate pickupDate;

    private LocalDate dropoffDate;

    private String bookingStatus;
    
    private String pickupLocation;
    
    private String dropoffLocation;
    
    private String tripDriverType;
    
    private Double total;
    
    private Double price; 
    

	public BookingDto() {
		super();
		// TODO Auto-generated constructor stub
	}


	public BookingDto(Integer userId, Integer carId, Integer days, LocalDate pickupDate, LocalDate dropoffDate,
			String bookingStatus, String pickupLocation, String dropoffLocation, String tripDriverType, Double total,
			Double price) {
		super();
		this.userId = userId;
		this.carId = carId;
		this.days = days;
		this.pickupDate = pickupDate;
		this.dropoffDate = dropoffDate;
		this.bookingStatus = bookingStatus;
		this.pickupLocation = pickupLocation;
		this.dropoffLocation = dropoffLocation;
		this.tripDriverType = tripDriverType;
		this.total = total;
		this.price = price;
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


	public Integer getDays() {
		return days;
	}


	public void setDays(Integer days) {
		this.days = days;
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


	public String getBookingStatus() {
		return bookingStatus;
	}


	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
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


	public Double getPrice() {
		return price;
	}


	public void setPrice(Double price) {
		this.price = price;
	}

	
	
}