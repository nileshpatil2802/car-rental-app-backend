package com.My_Car_Rental_Application.service;

import java.util.List;

import com.My_Car_Rental_Application.dto.BookingResponseDto;
import com.My_Car_Rental_Application.dto.CarsDataRequestDto;
import com.My_Car_Rental_Application.entity.AdminCarsData;
import com.My_Car_Rental_Application.entity.Booking;
import com.My_Car_Rental_Application.entity.UserDocuments;
import com.My_Car_Rental_Application.entity.UserRequest;
public interface AdminService {

	AdminCarsData addCars(CarsDataRequestDto adRequest);

	AdminCarsData updateCar(CarsDataRequestDto updateRequest, int id);

	void deleteCarById(int id);

	void deleteUserById(int id);

	UserRequest updateUserStatus(int id);

	UserRequest updateUserRole(UserRequest userRequest, int id);
	
	public List<Booking> getPendingBookingData();

	Booking updateBookingStatus(int bookingId, String bookingStatus);

	List<BookingResponseDto> BookingRecords();

	void deleteBooking(int bookingId);

	UserDocuments UpdateDocumentStatus(int userId,String status,String reason);

}
