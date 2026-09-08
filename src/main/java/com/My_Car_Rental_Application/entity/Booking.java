package com.My_Car_Rental_Application.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserRequest user;
    
    @ManyToOne
    @JoinColumn(name = "car_id")
    private AdminCarsData adminCarsData;
    
    private LocalDate pickupDate;

    private LocalDate dropoffDate;
    
    private int days;

    private String bookingStatus;
    
    private String pickupLocation;
    
    private String dropoffLocation;
    
    private String tripDriverType;
    
    private Double total;
    
    private Double price; 
    
    private String bookingSource;
    
    private String whatsappStatus;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

	public Booking() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Booking(int id, UserRequest user, AdminCarsData adminCarsData, LocalDate pickupDate, LocalDate dropoffDate,
			int days, String bookingStatus, String pickupLocation, String dropoffLocation, String tripDriverType,
			Double total, Double price, String bookingSource, String whatsappStatus, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.user = user;
		this.adminCarsData = adminCarsData;
		this.pickupDate = pickupDate;
		this.dropoffDate = dropoffDate;
		this.days = days;
		this.bookingStatus = bookingStatus;
		this.pickupLocation = pickupLocation;
		this.dropoffLocation = dropoffLocation;
		this.tripDriverType = tripDriverType;
		this.total = total;
		this.price = price;
		this.bookingSource = bookingSource;
		this.whatsappStatus = whatsappStatus;
		this.createdAt = createdAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserRequest getUser() {
		return user;
	}

	public void setUser(UserRequest user) {
		this.user = user;
	}

	public AdminCarsData getAdminCarsData() {
		return adminCarsData;
	}

	public void setAdminCarsData(AdminCarsData adminCarsData) {
		this.adminCarsData = adminCarsData;
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

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
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

	public String getBookingSource() {
		return bookingSource;
	}

	public void setBookingSource(String bookingSource) {
		this.bookingSource = bookingSource;
	}

	public String getWhatsappStatus() {
		return whatsappStatus;
	}

	public void setWhatsappStatus(String whatsappStatus) {
		this.whatsappStatus = whatsappStatus;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	
	
}