package com.My_Car_Rental_Application.dto;

import java.time.LocalDate;

public class WhatsAppBookingRequest {

    private Integer userId;
    private Integer carId;

    private LocalDate pickupDate;
    private LocalDate dropoffDate;

    private String pickupLocation;
    private String dropoffLocation;

    private String tripDriverType;

    private Integer days;
    private Double price;
    private Double total;
	
    public WhatsAppBookingRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	
    public WhatsAppBookingRequest(Integer userId, Integer carId, LocalDate pickupDate, LocalDate dropoffDate,
			String pickupLocation, String dropoffLocation, String tripDriverType, Integer days, Double price,
			Double total) {
		super();
		this.userId = userId;
		this.carId = carId;
		this.pickupDate = pickupDate;
		this.dropoffDate = dropoffDate;
		this.pickupLocation = pickupLocation;
		this.dropoffLocation = dropoffLocation;
		this.tripDriverType = tripDriverType;
		this.days = days;
		this.price = price;
		this.total = total;
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

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

    
    
}