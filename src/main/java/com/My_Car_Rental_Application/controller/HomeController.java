package com.My_Car_Rental_Application.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.My_Car_Rental_Application.dto.ApiResponse;
import com.My_Car_Rental_Application.dto.ForgotPasswordRequest;
import com.My_Car_Rental_Application.dto.ResetPasswordRequest;
import com.My_Car_Rental_Application.dto.UserLoginDto;
import com.My_Car_Rental_Application.dto.UserRequestDto;
import com.My_Car_Rental_Application.dto.UserResponseDto;
import com.My_Car_Rental_Application.entity.AdminCarsData;
import com.My_Car_Rental_Application.service.HomeService;
import com.My_Car_Rental_Application.service.PasswordResetService;

import jakarta.validation.Valid;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/home")
public class HomeController {
	
	private HomeService homeService;
	private AuthenticationManager authenticationManager;
	private final PasswordResetService passwordResetService;

	// constructor injection
	public HomeController(HomeService userService,AuthenticationManager authenticationManager,PasswordResetService passwordResetService) {
		this.homeService=userService;
		this.authenticationManager=authenticationManager;
		this.passwordResetService=passwordResetService;
		
	}
	
	
	
	@PostMapping("/register")
	public ResponseEntity<Map<String,String>> UserRegister(@RequestBody UserRequestDto request) {
		System.out.println("request : "+request);
		
		String doRegister = homeService.doRegister(request);
		
		
		return ResponseEntity.ok(Map.of("doRegister",doRegister));
	
	}
	

//	@PostMapping("/login")
//	public ResponseEntity<Map<String, Object>> doLogin(@RequestBody UserLoginDto request) {
//		System.out.println("Login Request : "+request.getEmail()+""+request.getPassword());
//		try {
//		Authentication authentication = authenticationManager.authenticate(
//	            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//	        );
//		System.out.println("authentication : "+authentication);
//		if(authentication.isAuthenticated()){
//			UserResponseDto user = homeService.doLogin(request);
//			return ResponseEntity.ok(Map.of(
//            "message", "Login Successful",
//            "user", user
//        ));
//		}
//		}catch(Exception e) {
//
//			System.out.println("❌ Auth Error: " + e.getMessage()); // ✅ See exact error
//	        return ResponseEntity.status(401).body(Map.of(
//	            "message", "Invalid Credentials",
//	            "error", e.getMessage()
//	        ));
//		}
//		return ResponseEntity.status(401).body(Map.of("message", "Invalid Credentials"));
//	}
	
//	@PostMapping("/login")
//	public ResponseEntity<Map<String, Object>> doLogin(@RequestBody UserLoginDto request) {
//
//	    System.out.println("Login Request : " + request.getEmail() + " " + request.getPassword());
//
//	    try {
//	        Authentication authentication = authenticationManager.authenticate(
//	                new UsernamePasswordAuthenticationToken(
//	                        request.getEmail(),
//	                        request.getPassword()
//	                )
//	        );
//
//	        if (authentication.isAuthenticated()) {
//	            UserResponseDto user = homeService.doLogin(request);
//
//	            return ResponseEntity.ok(Map.of(
//	                    "message", "Login Successful",
//	                    "user", user
//	            ));
//	        }
//
//	    } catch (ResponseStatusException e) {
//	        return ResponseEntity.status(e.getStatusCode()).body(Map.of(
//	                "message", e.getReason()
//	        ));
//
//	    } catch (Exception e) {
//	        System.out.println("❌ Auth Error: " + e.getMessage());
//
//	        return ResponseEntity.status(401).body(Map.of(
//	                "message", "Invalid Credentials",
//	                "error", e.getMessage()
//	        ));
//	    }
//
//	    return ResponseEntity.status(401).body(Map.of("message", "Invalid Credentials"));
//	}
	
//	@PostMapping("/login")
//	public ResponseEntity<Map<String, Object>> doLogin(@RequestBody UserLoginDto request) {
//
//	    System.out.println("Login Request : " + request.getEmail() + " " + request.getPassword());
//
//	    try {
//	        // 1. First check user exists and blocked status
//	        homeService.checkUserStatus(request);
//
//	        // 2. Then authenticate email/password
//	        Authentication authentication = authenticationManager.authenticate(
//	                new UsernamePasswordAuthenticationToken(
//	                        request.getEmail(),
//	                        request.getPassword()
//	                )
//	        );
//
//	        if (authentication.isAuthenticated()) {
//	            UserResponseDto user = homeService.doLogin(request);
//
//	            return ResponseEntity.ok(Map.of(
//	                    "message", "Login Successful",
//	                    "user", user
//	            ));
//	        }
//
//	    } catch (ResponseStatusException e) {
//	        return ResponseEntity.status(e.getStatusCode()).body(Map.of(
//	                "message", e.getReason()
//	        ));
//
//	    } catch (Exception e) {
//	        System.out.println("❌ Auth Error: " + e.getMessage());
//
//	        return ResponseEntity.status(401).body(Map.of(
//	                "message", "Invalid Credentials",
//	                "error", e.getMessage()
//	        ));
//	    }
//
//	    return ResponseEntity.status(401).body(Map.of("message", "Invalid Credentials"));
//	}
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> doLogin(@RequestBody UserLoginDto request) {

	    try {
	        // 1. First check email + password
	        Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(
	                        request.getEmail(),
	                        request.getPassword()
	                )
	        );

	        if (authentication.isAuthenticated()) {

	            // 2. After correct password, check blocked status
	            homeService.checkUserStatus(request);

	            // 3. Return user data
	            UserResponseDto user = homeService.doLogin(request);
	            

	            return ResponseEntity.ok(Map.of(
	                    "message", "Login Successful",
	                    "user", user
	            ));
	        }

	    } catch (BadCredentialsException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
	                "message", "Wrong Email or Password"
	        ));

	    } catch (ResponseStatusException e) {
	        return ResponseEntity.status(e.getStatusCode()).body(Map.of(
	                "message", e.getReason()
	        ));
	    }

	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
	            "message", "Invalid Credentials"
	    ));
	}
	
	@GetMapping("/cars")
	public List<AdminCarsData> allCars() {
		 List<AdminCarsData> allCars = homeService.allCars();
		 System.out.println("allCars : "+allCars);
		return allCars;
	}

	@GetMapping("/cars/{id}")
	public AdminCarsData findCarById(@PathVariable int id) {
		return homeService.findCarById(id);
	}
	
	@PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(
            @Valid
            @RequestBody
            ForgotPasswordRequest request
    ) {

        passwordResetService
                .createForgotPasswordRequest(
                        request.getEmail()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "If an account exists with this email, password reset instructions have been sent."
                )
        );
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<Map<String, Boolean>>
    validateResetToken(
            @RequestParam String token
    ) {

        boolean valid =
                passwordResetService
                        .validateToken(token);

        return ResponseEntity.ok(
                Map.of("valid", valid)
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(
            @Valid
            @RequestBody
            ResetPasswordRequest request
    ) {

        passwordResetService
                .resetPassword(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Password changed successfully. You can now log in with your new password."
                )
        );
    }
	


}
