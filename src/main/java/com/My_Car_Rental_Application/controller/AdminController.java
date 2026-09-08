
package com.My_Car_Rental_Application.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.My_Car_Rental_Application.dto.BookingResponseDto;
import com.My_Car_Rental_Application.dto.CarsDataRequestDto;
import com.My_Car_Rental_Application.entity.AdminCarsData;
import com.My_Car_Rental_Application.entity.Booking;
import com.My_Car_Rental_Application.entity.UserDocuments;
import com.My_Car_Rental_Application.entity.UserRequest;
import com.My_Car_Rental_Application.service.AdminService;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth/admin")
public class AdminController {
	
	private AdminService adService;
	
	public AdminController(AdminService adService) {
		this.adService=adService;
	}

	@PostMapping("/addCars")
	public AdminCarsData addCars(@RequestBody CarsDataRequestDto adRequest ) {
		return adService.addCars(adRequest);
	}
	
	@PostMapping("/updateCar/{id}") 		
	public AdminCarsData UpdateCar(@RequestBody CarsDataRequestDto updateRequest,@PathVariable int id) {
		
		return adService.updateCar(updateRequest, id);
	}
	
	@PostMapping("/deleteCarById/{id}")
	public void deleteCarById(@PathVariable int id) {
		adService.deleteCarById(id);
	}
	
	@PostMapping("/deleteUserById/{id}")
	public void deleteUserById(@PathVariable int id) {
		System.out.println("Id in deleteUserById : "+id);
		adService.deleteUserById(id);
	}
	
	@PostMapping("/updateUserStatus/{id}")
	public ResponseEntity<UserRequest> updateUserStatus(@PathVariable int id) {
	    UserRequest updatedUserStatus = adService.updateUserStatus(id);
	    return ResponseEntity.ok(updatedUserStatus);
	}
	
	@PostMapping("/updateUserRole/{id}")
	public ResponseEntity<UserRequest> updateUserRole(@RequestBody UserRequest userRequest,@PathVariable int id) {
	    UserRequest updatedUserRole = adService.updateUserRole(userRequest,id);
	    return ResponseEntity.ok(updatedUserRole);
	} 
	
	@GetMapping("/getPendingBookingsData")
	public List<Booking> GetPendingBookingsData() {
		return adService.getPendingBookingData();
	}
	
	@PostMapping("/updateBookingStatus/{bookingId}")
	public Booking updateBookingStatus(@PathVariable("bookingId") int bookingId,@RequestParam String bookingStatus) {
		return adService.updateBookingStatus(bookingId,bookingStatus);
	}
	
	@GetMapping("/getBookingRecords")
	public List<BookingResponseDto> BookingRecords() {
		List<BookingResponseDto> bookingRecords = adService.BookingRecords();
		System.out.println("Booking Records : "+bookingRecords);
		return bookingRecords;
	}
	
	@PostMapping("/deleteBookingById/{bookingId}")
	public String DeleteBooking(@PathVariable int bookingId) {
	
		adService.deleteBooking(bookingId);
		return "Data Deleted Successfully";
	}
	
	@PostMapping("/updateDocumentStatus/{id}")
	public UserDocuments UpdateDocumentStatus(@PathVariable int id,@RequestParam String status,@RequestParam String reason) {
		
		
		return adService.UpdateDocumentStatus(id,status,reason);
	}
	
	
	
}
