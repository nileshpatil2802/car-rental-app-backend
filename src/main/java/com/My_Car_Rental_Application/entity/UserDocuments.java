package com.My_Car_Rental_Application.entity;



import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class UserDocuments {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(length = 500)
	private String drivingLicense;
	@Column(length = 500)
	private String aadhharCard;
	
	
    @Size(min = 15, max = 16, message = "Driving Licence Number must be between 15 and 16 characters")
    @Pattern(
        regexp = "^[A-Z]{2}[0-9]{13,14}$",
        message = "Invalid Driving Licence Number"
    )
    @Column(length = 20)
    private String drivingLicenseNo;
	
	 private LocalDate licenseExpiry;

    @Pattern(
        regexp = "^[2-9][0-9]{11}$",
        message = "Aadhaar Number must contain exactly 12 digits"
    )
    @Column(length = 12)
    private String aadharNo;
	
	private String status;
	
	private String reason;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private UserRequest user;

	public UserDocuments() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserDocuments(int id, String drivingLicense, String aadhharCard,
			 @Size(min = 15, max = 16, message = "Driving Licence Number must be between 15 and 16 characters") @Pattern(regexp = "^[A-Z]{2}[0-9]{13,14}$", message = "Invalid Driving Licence Number") String drivingLicenseNo,
			LocalDate licenseExpiry,
			 @Pattern(regexp = "^[2-9][0-9]{11}$", message = "Aadhaar Number must contain exactly 12 digits") String aadharNo,
			String status, String reason, UserRequest user) {
		super();
		this.id = id;
		this.drivingLicense = drivingLicense;
		this.aadhharCard = aadhharCard;
		this.drivingLicenseNo = drivingLicenseNo;
		this.licenseExpiry = licenseExpiry;
		this.aadharNo = aadharNo;
		this.status = status;
		this.reason = reason;
		this.user = user;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDrivingLicense() {
		return drivingLicense;
	}

	public void setDrivingLicense(String drivingLicense) {
		this.drivingLicense = drivingLicense;
	}

	public String getAadhharCard() {
		return aadhharCard;
	}

	public void setAadhharCard(String aadhharCard) {
		this.aadhharCard = aadhharCard;
	}

	public String getDrivingLicenseNo() {
		return drivingLicenseNo;
	}

	public void setDrivingLicenseNo(String drivingLicenseNo) {
		this.drivingLicenseNo = drivingLicenseNo;
	}

	public LocalDate getLicenseExpiry() {
		return licenseExpiry;
	}

	public void setLicenseExpiry(LocalDate licenseExpiry) {
		this.licenseExpiry = licenseExpiry;
	}

	public String getAadharNo() {
		return aadharNo;
	}

	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public UserRequest getUser() {
		return user;
	}

	public void setUser(UserRequest user) {
		this.user = user;
	}

	
	
	
}
