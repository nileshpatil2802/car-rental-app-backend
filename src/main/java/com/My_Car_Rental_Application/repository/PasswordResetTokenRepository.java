package com.My_Car_Rental_Application.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.My_Car_Rental_Application.entity.PasswordResetToken;
import com.My_Car_Rental_Application.entity.UserRequest;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	Optional<PasswordResetToken> findByTokenHash(String tokenHash);

	List<PasswordResetToken> findAllByUserAndUsedFalse(UserRequest user);

	long deleteByExpiryDateBefore(LocalDateTime dateTime);
}
