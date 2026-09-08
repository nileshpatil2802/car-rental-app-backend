package com.My_Car_Rental_Application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.My_Car_Rental_Application.entity.Cart;


@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {



	List<Cart> findByUser_Id(int userId);

	@Query("SELECT c FROM Cart c WHERE c.id = :id AND c.user.id = :userId")
	Cart findByIdAndUserId(@Param("id") int id, @Param("userId") int userId);

	boolean existsByUser_IdAndCar_Id(int id, int id2);
}
