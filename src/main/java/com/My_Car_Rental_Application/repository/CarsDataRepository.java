package com.My_Car_Rental_Application.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.My_Car_Rental_Application.entity.AdminCarsData;


@Repository
public interface CarsDataRepository extends JpaRepository<AdminCarsData, Integer>{


}
