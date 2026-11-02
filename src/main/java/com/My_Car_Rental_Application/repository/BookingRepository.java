package com.My_Car_Rental_Application.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.My_Car_Rental_Application.dto.BookingDto;
import com.My_Car_Rental_Application.dto.BookingResponseDto;
import com.My_Car_Rental_Application.entity.Booking;

import jakarta.transaction.Transactional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

	List<Booking> findByUser_Id(int userId);

	@Modifying
	@Transactional
	@Query("DELETE FROM Booking b WHERE b.id = :id AND b.user.id = :userId")
	void deleteByBookingIdAndUserId(@Param("id") int id, @Param("userId") int userId);

//	@Query("""
//		    SELECT COUNT(b)
//		    FROM Booking b
//		    WHERE b.car.id = :carId
//		    AND UPPER(b.bookingStatus) IN ('PENDING', 'CONFIRMED')
//		    AND b.pickupDate <= :dropoffDate
//		    AND b.dropoffDate >= :pickupDate
//		""")
//		long countOverlappingBookings(
//		        @Param("carId") int carId,
//		        @Param("pickupDate") LocalDate pickupDate,
//		        @Param("dropoffDate") LocalDate dropoffDate
//		);	
//	@Query("""
//	        SELECT COUNT(b)
//	        FROM Booking b
//	        WHERE b.carId = :carId
//	        AND UPPER(b.bookingStatus) IN ('PENDING', 'CONFIRMED')
//	        AND b.pickupDate <= :dropoffDate
//	        AND b.dropoffDate >= :pickupDate
//	    """)
//	    long countOverlappingBookings(
//	            @Param("carId") int carId,
//	            @Param("pickupDate") LocalDate pickupDate,
//	            @Param("dropoffDate") LocalDate dropoffDate
//	    );
	@Query("""
			    SELECT COUNT(b)
			    FROM Booking b
			    WHERE b.adminCarsData.id = :carId
			    AND UPPER(b.bookingStatus) IN ('PENDING', 'CONFIRMED')
			    AND b.pickupDate <= :dropoffDate
			    AND b.dropoffDate >= :pickupDate
			""")
	long countOverlappingBookings(@Param("carId") int carId, @Param("pickupDate") LocalDate pickupDate,
			@Param("dropoffDate") LocalDate dropoffDate);

	// fetching pending and rejected
	List<Booking> findByBookingStatusInOrderByIdDesc(List<String> bookingStatus);

	// fetching remaining status
	List<Booking> findByBookingStatusIn(List<String> bookingStatus);

	@Query(value = "SELECT * FROM booking b " + "WHERE b.car_id = :car_id "
			+ "AND b.user_id = :user_id", nativeQuery = true)
	Booking findTripDetailsByCarIdAndUserId(@Param("car_id") int car_id, @Param("user_id") int user_id);

}
