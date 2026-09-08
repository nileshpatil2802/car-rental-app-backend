package com.My_Car_Rental_Application.service;




public interface EmailService {
	public void sendPasswordResetEmail(
            String recipientEmail,
            String firstName,
            String resetLink,
            long expirationMinutes
    ) ;

}
