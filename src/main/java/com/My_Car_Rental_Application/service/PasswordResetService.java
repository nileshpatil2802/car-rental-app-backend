package com.My_Car_Rental_Application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.My_Car_Rental_Application.dto.ResetPasswordRequest;
import com.My_Car_Rental_Application.entity.PasswordResetToken;
import com.My_Car_Rental_Application.entity.UserRequest;
import com.My_Car_Rental_Application.repository.HomeRepository;
import com.My_Car_Rental_Application.repository.PasswordResetTokenRepository;
import com.My_Car_Rental_Application.util.ResetTokenUtil;

@Service
public class PasswordResetService{

    private final HomeRepository homeRepository;

    private final PasswordResetTokenRepository
            passwordResetTokenRepository;

    private final EmailService emailService;

    private final ResetTokenUtil resetTokenUtil;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.reset-password-url}")
    private String frontendResetPasswordUrl;

    @Value("${app.password-reset.expiration-minutes:15}")
    private long expirationMinutes;

    public PasswordResetService(
    		HomeRepository homeRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService,
            ResetTokenUtil resetTokenUtil,
            PasswordEncoder passwordEncoder
    ) {
        this.homeRepository = homeRepository;
        this.passwordResetTokenRepository =
                passwordResetTokenRepository;
        this.emailService = emailService;
        this.resetTokenUtil = resetTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    
    @Transactional
    public void createForgotPasswordRequest(
            String requestedEmail
    ) {

        String normalizedEmail =
                normalizeEmail(requestedEmail);

        UserRequest user = homeRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElse(null);

        /*
         * Do not throw "user not found".
         * It prevents attackers from checking registered emails.
         */
        if (user == null) {
            return;
        }

        LocalDateTime currentTime =
                LocalDateTime.now();

        invalidatePreviousTokens(
                user,
                currentTime
        );

        String rawToken =
                resetTokenUtil.generateToken();

        String hashedToken =
                resetTokenUtil.hashToken(rawToken);

        PasswordResetToken resetToken =
                new PasswordResetToken();

        resetToken.setUser(user);
        resetToken.setTokenHash(hashedToken);
        resetToken.setCreatedAt(currentTime);
        resetToken.setExpiryDate(
                currentTime.plusMinutes(
                        expirationMinutes
                )
        );
        resetToken.setUsed(false);

        passwordResetTokenRepository.save(
                resetToken
        );

        String resetLink = UriComponentsBuilder
                .fromUriString(
                        frontendResetPasswordUrl
                )
                .queryParam("token", rawToken)
                .build()
                .encode()
                .toUriString();

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getFirstName(),
                resetLink,
                expirationMinutes
        );
    }

    
    @Transactional(readOnly = true)
    public boolean validateToken(
            String rawToken
    ) {

        if (rawToken == null || rawToken.isBlank()) {
            return false;
        }

        String hashedToken =
                resetTokenUtil.hashToken(rawToken);

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByTokenHash(hashedToken)
                        .orElse(null);

        if (resetToken == null) {
            return false;
        }

        if (resetToken.isUsed()) {
            return false;
        }

        return resetToken
                .getExpiryDate()
                .isAfter(LocalDateTime.now());
    }


    @Transactional
    public void resetPassword(
            ResetPasswordRequest request
    ) {

        validatePasswords(request);

        String hashedToken =
                resetTokenUtil.hashToken(
                        request.getToken()
                );

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByTokenHash(hashedToken)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid password reset link"
                                )
                        );

        LocalDateTime currentTime =
                LocalDateTime.now();

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException(
                    "This password reset link has already been used"
            );
        }

        if (
                resetToken
                        .getExpiryDate()
                        .isBefore(currentTime)
        ) {
            throw new IllegalArgumentException(
                    "This password reset link has expired"
            );
        }

        UserRequest user =
                resetToken.getUser();

        if (
                passwordEncoder.matches(
                        request.getNewPassword(),
                        user.getPassword()
                )
        ) {
            throw new IllegalArgumentException(
                    "New password must be different from the current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        homeRepository.save(user);

        resetToken.setUsed(true);
        resetToken.setUsedAt(currentTime);

        passwordResetTokenRepository.save(
                resetToken
        );

        invalidateOtherTokens(
                user,
                resetToken.getId(),
                currentTime
        );
    }

    private void validatePasswords(
            ResetPasswordRequest request
    ) {

        if (
                !request
                        .getNewPassword()
                        .equals(
                                request.getConfirmPassword()
                        )
        ) {
            throw new IllegalArgumentException(
                    "New password and confirm password do not match"
            );
        }

        String password =
                request.getNewPassword();

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters"
            );
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter"
            );
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one lowercase letter"
            );
        }

        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one number"
            );
        }

        if (!password.matches(".*[^A-Za-z0-9].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one special character"
            );
        }
    }

    private void invalidatePreviousTokens(
            UserRequest user,
            LocalDateTime usedAt
    ) {

        List<PasswordResetToken> activeTokens =
                passwordResetTokenRepository
                        .findAllByUserAndUsedFalse(user);

        for (
                PasswordResetToken token
                : activeTokens
        ) {
            token.setUsed(true);
            token.setUsedAt(usedAt);
        }

        passwordResetTokenRepository.saveAll(
                activeTokens
        );
    }

    private void invalidateOtherTokens(
            UserRequest user,
            Long currentTokenId,
            LocalDateTime usedAt
    ) {

        List<PasswordResetToken> activeTokens =
                passwordResetTokenRepository
                        .findAllByUserAndUsedFalse(user);

        for (
                PasswordResetToken token
                : activeTokens
        ) {
            if (
                    !token
                            .getId()
                            .equals(currentTokenId)
            ) {
                token.setUsed(true);
                token.setUsedAt(usedAt);
            }
        }

        passwordResetTokenRepository.saveAll(
                activeTokens
        );
    }

    private String normalizeEmail(
            String email
    ) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email address is required"
            );
        }

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
