package com.My_Car_Rental_Application.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailServiceImpl implements EmailService{
	
	private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendPasswordResetEmail(
            String recipientEmail,
            String firstName,
            String resetLink,
            long expirationMinutes
    ) {

        String userName =
                firstName == null || firstName.isBlank()
                        ? "User"
                        : firstName;

        String emailBody = """
                Hello %s,

                We received a request to reset your SelfDrive Junction password.

                Click the link below to create a new password:

                %s

                This link will expire in %d minutes and can be used only once.

                If you did not request this password reset, you can safely ignore this email.

                Regards,
                SelfDrive Junction Team
                """.formatted(
                userName,
                resetLink,
                expirationMinutes
        );

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject(
                "Reset your SelfDrive Junction password"
        );
        message.setText(emailBody);

        try {
            javaMailSender.send(message);
        } catch (MailException exception) {
            throw new IllegalStateException(
                    "Failed to send password reset email",
                    exception
            );
        }
    }
}
