package com.My_Car_Rental_Application.dto;

public class UserLoginDto {

	private String email;
    private String password;
  

    // Default Constructor
    public UserLoginDto() {
        super();
    }

    // Parameterized Constructor
    public UserLoginDto(String email, String password) {
        super();
        this.email = email;
        this.password = password;
       
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    

	
	
}
