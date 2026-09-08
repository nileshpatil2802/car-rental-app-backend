package com.My_Car_Rental_Application.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.My_Car_Rental_Application.dto.BookingResponseDto;
import com.My_Car_Rental_Application.dto.CarsDataRequestDto;
import com.My_Car_Rental_Application.entity.AdminCarsData;
import com.My_Car_Rental_Application.entity.Booking;
import com.My_Car_Rental_Application.entity.UserDocuments;
import com.My_Car_Rental_Application.entity.UserRequest;
import com.My_Car_Rental_Application.repository.BookingRepository;
import com.My_Car_Rental_Application.repository.CarsDataRepository;
import com.My_Car_Rental_Application.repository.HomeRepository;
import com.My_Car_Rental_Application.repository.UserDocumentRepository;

@Service
public class AdminServiceImpl implements AdminService {

	private static final String folder = "D:/CarRentalUploads/cars/";

	private CarsDataRepository carsDataRepository;

	private HomeRepository homeRepository;

	private BookingRepository bookingRepository;

	private UserDocumentRepository userDocumentRepository;

	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

	public AdminServiceImpl(CarsDataRepository carsDataRepository, HomeRepository homeRepository,
			BookingRepository bookingRepository, UserDocumentRepository userDocumentRepository) {
		this.carsDataRepository = carsDataRepository;
		this.homeRepository = homeRepository;
		this.bookingRepository = bookingRepository;
		this.userDocumentRepository = userDocumentRepository;

	}

	@Override
	public AdminCarsData addCars(CarsDataRequestDto adRequest) {

		try {

			Files.createDirectories(Paths.get(folder));
			String mainImageFilename = saveBase64Image(adRequest.getMainImage(), adRequest.getMainImageName());

			String img1Filename = saveBase64Image(adRequest.getImg1(), adRequest.getImg1Name());

			String img2Filename = saveBase64Image(adRequest.getImg2(), adRequest.getImg2Name());

			String img3Filename = saveBase64Image(adRequest.getImg3(), adRequest.getImg3Name());

			AdminCarsData ad = new AdminCarsData();
			ad.setDescription(adRequest.getDescription());
			ad.setFeatures(adRequest.getFeatures());
			ad.setFuelType(adRequest.getFuelType());
			ad.setMainImage(mainImageFilename);
			ad.setImg1(img1Filename);
			ad.setImg2(img2Filename);
			ad.setImg3(img3Filename);
			ad.setName(adRequest.getName());
			ad.setPrice(adRequest.getPrice());
			ad.setSeating(adRequest.getSeating());
			ad.setStatus(adRequest.isStatus());
			ad.setTransmition(adRequest.getTransmition());
			ad.setBrand(adRequest.getBrand());

			ad.setFeatures(adRequest.getFeatures());

			return carsDataRepository.save(ad);
		} catch (IOException exception) {
			throw new RuntimeException("Unable to save car images: " + exception.getMessage(), exception);
		}

	}
	
	private String saveBase64Image(
            String base64Image,
            String originalFilename)
            throws IOException {

        if (base64Image == null ||
                base64Image.isBlank()) {
            return null;
        }

        if (originalFilename == null ||
                originalFilename.isBlank()) {

            throw new IllegalArgumentException(
                    "Original image filename is missing"
            );
        }

        String safeFilename =
                Paths.get(originalFilename)
                        .getFileName()
                        .toString();

        validateImageExtension(safeFilename);

        /*
         * Incoming:
         * data:image/jpeg;base64,/9j/4AA...
         *
         * Decode only the part after comma.
         */
        String encodedContent = base64Image;

        int commaPosition =
                base64Image.indexOf(',');

        if (commaPosition >= 0) {
            encodedContent =
                    base64Image.substring(
                            commaPosition + 1
                    );
        }

        byte[] imageBytes;

        try {
            imageBytes =
                    Base64.getDecoder()
                            .decode(encodedContent);

        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid Base64 image data for: "
                            + safeFilename,
                    exception
            );
        }

        String savedFilename =
                UUID.randomUUID()
                        + "_"
                        + safeFilename;

        Path targetPath =
                Paths.get(
                        folder,
                        savedFilename
                );

        Files.write(
                targetPath,
                imageBytes
        );

        return savedFilename;
    }

    private void validateImageExtension(
            String filename) {

        String lower =
                filename.toLowerCase();

        boolean valid =
                lower.endsWith(".jpg")
                || lower.endsWith(".jpeg")
                || lower.endsWith(".png")
                || lower.endsWith(".webp")
                || lower.endsWith(".avif");

        if (!valid) {
            throw new IllegalArgumentException(
                    "Unsupported image format: "
                            + filename
            );
        }
    }


	@Override
	public AdminCarsData updateCar(CarsDataRequestDto updateRequest, int id) {
		AdminCarsData storedData = carsDataRepository.findById(id).orElse(null);
		storedData.setBrand(updateRequest.getBrand());
		storedData.setDescription(updateRequest.getDescription());
		storedData.setFeatures(updateRequest.getFeatures());
		storedData.setFuelType(updateRequest.getFuelType());
		storedData.setImg1(updateRequest.getImg1());
		storedData.setImg2(updateRequest.getImg2());
		storedData.setImg3(updateRequest.getImg3());
		storedData.setMainImage(updateRequest.getMainImage());
		storedData.setName(updateRequest.getName());
		storedData.setPrice(updateRequest.getPrice());

		storedData.setSeating(updateRequest.getSeating());
		storedData.setStatus(updateRequest.isStatus());
		storedData.setTransmition(updateRequest.getTransmition());

		return carsDataRepository.save(storedData);

	}

	@Override
	public void deleteCarById(int id) {
		carsDataRepository.deleteById(id);
	}

	@Override
	public void deleteUserById(int id) {
		homeRepository.deleteById(id);

	}

	@Override
	public UserRequest updateUserStatus(int id) {
		UserRequest user = homeRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

		if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {
			user.setStatus("BLOCKED");
		} else {
			user.setStatus("ACTIVE");
		}

		return homeRepository.save(user);
	}

	@Override
	public UserRequest updateUserRole(UserRequest userRequest, int id) {
		UserRequest user = homeRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
		user.setRole(userRequest.getRole());

		return homeRepository.save(user);
	}

	@Override
	public List<Booking> getPendingBookingData() {

		return bookingRepository.findByBookingStatusInOrderByIdDesc(List.of("PENDING", "REJECTED"));
	}

	@Override
	public Booking updateBookingStatus(int bookingId, String bookingStatus) {
		Booking bookingRecord = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		bookingRecord.setBookingStatus(bookingStatus);
		return bookingRepository.save(bookingRecord);
	}

	@Override
	public List<BookingResponseDto> BookingRecords() {

		List<Booking> byBookingStatusIn = bookingRepository.findByBookingStatusIn(List.of(

				"CONFIRMED", "CANCELLED", "ACTIVE", "COMPLETED"));
		System.out.println("Size = " + byBookingStatusIn.size());
		logger.info("Info of size : " + byBookingStatusIn.size());

		return byBookingStatusIn.stream().map(this::convertToDto).toList();

	}
	
//	public BookingResponseDto convertToDto(Booking booking) {
//		BookingResponseDto dto=new BookingResponseDto();
//		dto.setBookingId(booking.getId());
//		dto.setBrand(booking.getAdminCarsData().getBrand());
//		dto.setCarId(booking.getAdminCarsData().getId());
//		dto.setCreatedAt(booking.getCreatedAt());
//		dto.setDescription(booking.getAdminCarsData().getDescription());
//		dto.setFeatures(booking.getAdminCarsData().getFeatures());
//		dto.setFuelType(booking.getAdminCarsData().getFuelType());
//		dto.setMainImage(booking.getAdminCarsData().getMainImage());
//		dto.setImg1(booking.getAdminCarsData().getImg1());
//		dto.setImg2(booking.getAdminCarsData().getImg2());
//		dto.setImg3(booking.getAdminCarsData().getImg3());
//		dto.setName(booking.getAdminCarsData().getName());
//		dto.setPrice(booking.getAdminCarsData().getPrice());
//		dto.setSeating(booking.getAdminCarsData().getSeating());
//		dto.setStatus(booking.getBookingStatus());
//		dto.setTransmition(booking.getAdminCarsData().getTransmition());
//		dto.setUserId(booking.getUser().getId());
//		
//		return dto;
//	}

	private BookingResponseDto convertToDto(Booking booking) {

	    BookingResponseDto dto = new BookingResponseDto();

	    // Booking details
	    dto.setBookingId(booking.getId());
	    dto.setCreatedAt(booking.getCreatedAt());
	    dto.setStatus(booking.getBookingStatus());

	    dto.setPickupDate(booking.getPickupDate());
	    dto.setDropoffDate(booking.getDropoffDate());

	    dto.setPickupLocation(booking.getPickupLocation());
	    dto.setDropoffLocation(booking.getDropoffLocation());

	    dto.setTripDriverType(booking.getTripDriverType());

	    dto.setDays(booking.getDays());
	    dto.setTotal(booking.getTotal());
	    dto.setPrice(booking.getPrice());
	    dto.setName(booking.getUser().getFirstName());
	    dto.setUserEmail(booking.getUser().getEmail());

	    // User details
	    if (booking.getUser() != null) {
	        //dto.setUserId(booking.getUser().getId());
	    	UserRequest user=booking.getUser();
	    	System.out.println("I am in convert to Dto print user object :"+user);
	    	dto.setUserId(user.getId());
	    	dto.setFirstName(booking.getUser().getFirstName());
	    	dto.setLastName(booking.getUser().getLastName());
	    	dto.setUserEmail(user.getEmail());
	    	
	    	String firstName = booking.getUser().getFirstName() == null
	    	        ? ""
	    	        : booking.getUser().getFirstName();
	    	System.out.println("First Name :"+firstName);

	    	String lastName = booking.getUser().getLastName() == null
	    	        ? ""
	    	        : booking.getUser().getLastName();
	    	System.out.println("Last Name :"+lastName);

	    	dto.setUserName((firstName + " " + lastName).trim());
	    }

	    // Car details
	    if (booking.getAdminCarsData() != null) {

	        AdminCarsData car = booking.getAdminCarsData();

	        dto.setBrand(car.getBrand());
	        dto.setCarId(car.getId());
	        dto.setDescription(car.getDescription());
	        dto.setFeatures(car.getFeatures());
	        dto.setFuelType(car.getFuelType());

	        dto.setMainImage(car.getMainImage());
	        dto.setImg1(car.getImg1());
	        dto.setImg2(car.getImg2());
	        dto.setImg3(car.getImg3());

	       dto.setName(car.getName());
	        dto.setSeating(car.getSeating());
	        dto.setTransmition(car.getTransmition());
	        
	        
	    }

	    return dto;
	}
	
	@Override
	public void deleteBooking(int bookingId) {

		bookingRepository.deleteById(bookingId);
	}

	@Override
	public UserDocuments UpdateDocumentStatus(int id, String status, String reason) {

		UserDocuments document = userDocumentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Documents not found"));
		document.setStatus(status);
		if ("REJECT".equalsIgnoreCase(status)) {
			document.setReason(reason);
		} else {
			document.setReason(null);
		}

		return userDocumentRepository.save(document);
	}

}
