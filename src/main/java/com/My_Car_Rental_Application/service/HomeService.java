package com.My_Car_Rental_Application.service;


import java.util.List;

import com.My_Car_Rental_Application.dto.UserLoginDto;
import com.My_Car_Rental_Application.dto.UserRequestDto;
import com.My_Car_Rental_Application.dto.UserResponseDto;
import com.My_Car_Rental_Application.entity.AdminCarsData;
import com.My_Car_Rental_Application.entity.UserRequest;



public interface HomeService {

	String doRegister(UserRequestDto request);

	UserResponseDto doLogin(UserLoginDto request);
	
	List<AdminCarsData> allCars();

	AdminCarsData findCarById(int id);

	public void checkUserStatus(UserLoginDto request);

}
