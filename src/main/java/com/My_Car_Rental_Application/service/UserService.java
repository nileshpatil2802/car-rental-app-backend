package com.My_Car_Rental_Application.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.My_Car_Rental_Application.dto.AddToCartDto;
import com.My_Car_Rental_Application.dto.BookingDto;
import com.My_Car_Rental_Application.dto.BookingResponseDto;
import com.My_Car_Rental_Application.dto.CarsDataRequestDto;
import com.My_Car_Rental_Application.dto.CartResponseDto;
import com.My_Car_Rental_Application.entity.Booking;
import com.My_Car_Rental_Application.entity.Cart;
import com.My_Car_Rental_Application.entity.UserDocuments;
import com.My_Car_Rental_Application.entity.UserRequest;

public interface UserService {
	

	
	List<CartResponseDto> getAllCarts(int userId);

	CartResponseDto getCartById(int id,int userId);

	void deleteCart(int id);

	UserRequest getUserByEmail(String email);

	UserRequest updateUserProfile(UserRequest userRequest, String email);

	List<BookingResponseDto> booking(BookingDto booking);
	
	

	void cancelBooking(int bookingId, int userId);

	List<BookingResponseDto> getBookingList(int id);

	UserDocuments uploadDocuments(
	        int userId,String documentType,
	        MultipartFile drivingLicense,LocalDate licenseExpiry,
	        MultipartFile aadhharCard,String aadharCardNo,String drivingLicenseNo);
	
	UserDocuments updateDocument(
	        int userId,
	        String documentType,
	        MultipartFile drivingLicense,
	        LocalDate licenseExpiry,
	        MultipartFile aadhharCard,
	        String aadharCardNo,
	        String drivingLicenseNo
	);

	UserDocuments getDocumentsByUser(int userId);

	List<UserRequest> getAllUsers();

	List<UserDocuments> DocumentList();

	List<CartResponseDto> addCart(AddToCartDto acd);

	void deleteUserDocument(int userId, String documentType);


}
