package com.My_Car_Rental_Application.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.My_Car_Rental_Application.dto.UserLoginDto;
import com.My_Car_Rental_Application.dto.UserRequestDto;
import com.My_Car_Rental_Application.dto.UserResponseDto;
import com.My_Car_Rental_Application.entity.AdminCarsData;
import com.My_Car_Rental_Application.entity.UserDocuments;
import com.My_Car_Rental_Application.entity.UserRequest;
import com.My_Car_Rental_Application.repository.CarsDataRepository;
import com.My_Car_Rental_Application.repository.HomeRepository;
import com.My_Car_Rental_Application.repository.UserDocumentRepository;

@Service
public class HomeServiceImpl implements HomeService{

	private HomeRepository homeRepository;
	
	private CarsDataRepository cd;
	
	private UserDocumentRepository userDocumentRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	public HomeServiceImpl(HomeRepository userRepository,CarsDataRepository cd,PasswordEncoder passwordEncoder,UserDocumentRepository userDocumentRepository) {
		this.homeRepository=userRepository;
		this.cd=cd;
		this.passwordEncoder=passwordEncoder;
		this.userDocumentRepository=userDocumentRepository;
	}
	
	@Override
	public String doRegister(UserRequestDto request) {
		
		  if (homeRepository.existsByEmailIgnoreCase(request.getEmail())) {
	            throw new IllegalArgumentException(
	                    "Email address is already registered"
	            );
	        }
		
		
		UserRequest userRequest=new UserRequest();
		userRequest.setFirstName(request.getFirstName());
		userRequest.setLastName(request.getLastName());
		userRequest.setEmail(request.getEmail().trim().toLowerCase());
		userRequest.setPhone(request.getPhone());
		userRequest.setPassword(passwordEncoder.encode(request.getPassword()));
		userRequest.setAvatar(request.getAvatar());
		if(userRequest.getRole()==null  || userRequest.getRole().isBlank()) {
			userRequest.setRole("USER");
		}
	
		
		
		
		homeRepository.save(userRequest);
		return "User Register Successfully";
	}


//	@Override
//	public UserResponseDto doLogin(UserLoginDto request) {
//		UserRequest user= homeRepository.findByEmail(request.getEmail());
//		System.out.println("UserRequest : "+user);
//		
//		if(user != null ) {
//			
//			 if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
//		            throw new RuntimeException("Your account is blocked. Please contact admin.");
//		        }
//			
//			System.out.println("User : "+user);
//			return new UserResponseDto(user.getId(),user.getFirstName(),user.getLastName(),user.getEmail(),user.getPhone(),user.getAvatar(),user.getRole());
//		}else {
//			System.out.println("User is not Matched");
//		}
//		
//		return null;
//	}
	
//	@Override
//	public UserResponseDto doLogin(UserLoginDto request) {
//
//	    UserRequest user = homeRepository.findByEmail(request.getEmail());
//
//	    if (user == null) {
//	        throw new ResponseStatusException(
//	                HttpStatus.UNAUTHORIZED,
//	                "Invalid Email"
//	        );
//	    }
//
//	    if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
//	        throw new ResponseStatusException(
//	                HttpStatus.FORBIDDEN,
//	                "Your account has been blocked. Please contact admin."
//	        );
//	    }
//
//	    return new UserResponseDto(
//	            user.getId(),
//	            user.getFirstName(),
//	            user.getLastName(),
//	            user.getEmail(),
//	            user.getPhone(),
//	            user.getAvatar(),
//	            user.getRole()
//	    );
//	}
	
	@Override
	public UserResponseDto doLogin(UserLoginDto request) {

	    UserRequest user = homeRepository.findByEmail(request.getEmail());

	    return new UserResponseDto(
	            user.getId(),
	            user.getFirstName(),
	            user.getLastName(),
	            user.getEmail(),
	            user.getPhone(),
	            user.getAvatar(),
	            user.getRole()
	    );
	}
	
	public void checkUserStatus(UserLoginDto request) {

	    UserRequest user = homeRepository.findByEmail(request.getEmail());

	    if (user == null) {
	        throw new ResponseStatusException(
	                HttpStatus.UNAUTHORIZED,
	                "Invalid Email"
	        );
	    }

	    if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
	        throw new ResponseStatusException(
	                HttpStatus.FORBIDDEN,
	                "Your account has been blocked. Please contact admin."
	        );
	    }  
	    
	    
	    
	}
	
	@Override
	public List<AdminCarsData> allCars() {
		return cd.findAll();
	}

	@Override
	public AdminCarsData findCarById(int id) {
		return cd.findById(id).orElse(null);
	}
	
	



}
