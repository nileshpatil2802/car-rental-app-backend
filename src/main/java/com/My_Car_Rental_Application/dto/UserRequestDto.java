package com.My_Car_Rental_Application.dto;

public class UserRequestDto {

	private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String avatar;
	
    public UserRequestDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserRequestDto(String firstName, String lastName, String email, String phone, String password,
			String avatar) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.password = password;
		this.avatar = avatar;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public String toString() {
		return "UserRequestDto [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", phone="
				+ phone + ", password=" + password + ", avatar=" + avatar + "]";
	}
	
    
    
    
}
