package com.My_Car_Rental_Application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.My_Car_Rental_Application.entity.AdminCarsData;
import com.My_Car_Rental_Application.entity.UserDocuments;
import com.My_Car_Rental_Application.entity.UserRequest;

@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocuments, Integer>{

	Optional<UserDocuments> findByUserId(int userId);
	
	UserDocuments findByUser_Id(int userId);

	Optional<UserDocuments> findByUser(UserRequest user);

}
