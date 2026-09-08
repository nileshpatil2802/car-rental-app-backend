	package com.My_Car_Rental_Application.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.My_Car_Rental_Application.dto.AddToCartDto;
import com.My_Car_Rental_Application.dto.BookingDto;
import com.My_Car_Rental_Application.dto.BookingResponseDto;
import com.My_Car_Rental_Application.dto.CartResponseDto;
import com.My_Car_Rental_Application.entity.UserDocuments;
import com.My_Car_Rental_Application.entity.UserRequest;
import com.My_Car_Rental_Application.repository.UserDocumentRepository;
import com.My_Car_Rental_Application.service.UserService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/user")
public class UserController {
	
	private UserService userService;
	
	private UserDocumentRepository userDocumentRepository;
	
	public UserController(UserService userService,UserDocumentRepository userDocumentRepository) {
		this.userService=userService;
		this.userDocumentRepository=userDocumentRepository;  
	}
	

	@PostMapping("/cart")
	public List<CartResponseDto> addCart(@RequestBody AddToCartDto dto){
		System.out.println("Received User Id = " + dto.getUserId());
		List<CartResponseDto> cart = userService.addCart(dto);
		System.out.println("I am in cart");
		System.out.println("cart : "+cart);
		return cart;
	}
	
	@GetMapping("/getCarts/{userId}")
	public List<CartResponseDto> getCarts(@PathVariable int userId){
	 System.out.println("Get carts : "+userId);
	 List<CartResponseDto> allCarts = userService.getAllCarts(userId);
	 	System.out.println("allCarts : "+allCarts);
	    return allCarts;
	}
	
	@GetMapping("/getCarts/{id}/{userId}")
	public  CartResponseDto getCart(@PathVariable int id,@PathVariable int userId) {
		return userService.getCartById(id,userId);
	}
	
	@PostMapping("deleteCarts/{id}")
	public void DeleteCart(@PathVariable int id) {
		userService.deleteCart(id);
	}
	
	@GetMapping("/getUserByEmail/{email}")
	public UserRequest getUserByEmail(@PathVariable String email) {
		UserRequest userByEmail = userService.getUserByEmail(email);
		System.out.println("userByEmail : "+userByEmail.getPassword());
		return userByEmail;
	}
	
	@GetMapping("/getAllUsers")
	public List<UserRequest> getAllUsers(){
		return userService.getAllUsers();
	}
	
	@PostMapping("/updateProfile/{email}")
	public UserRequest updateUserProfile(@RequestBody UserRequest userRequest,@PathVariable String email) {
		UserRequest updateUserProfile = userService.updateUserProfile(userRequest,email);
		System.out.println("Updated Profile : "+updateUserProfile);
		return updateUserProfile;
	}
	
//	@PostMapping("/booking")
//	public List<BookingResponseDto> booking(@RequestBody BookingDto booking){
//		
//		return userService.booking(booking);
//	}
	
//	@PostMapping("/booking")
//	public ResponseEntity<?> booking(@RequestBody BookingDto booking) {
//
//	    try {
//
//	        List<BookingResponseDto> response =
//	                userService.booking(booking);
//
//	        return ResponseEntity.ok(response);
//
//	    } catch (RuntimeException e) {
//
//	        return ResponseEntity
//	                .badRequest()
//	                .body(Map.of("message", e.getMessage()));
//	    }
//	}
	@PostMapping("/booking")
	public ResponseEntity<?> booking(@RequestBody BookingDto booking) {
	    try {
	        List<BookingResponseDto> response = userService.booking(booking);
	        return ResponseEntity.ok(response);
	    } catch (RuntimeException e) {
	        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
	    }
	}
	
	@GetMapping("/bookingList/{id}")
	public List<BookingResponseDto> BookingList(@PathVariable int id){
		return userService.getBookingList(id);
	}
	
	@PostMapping("/cancelBooking/{bookingId}/{userId}")
	public void cancelBooking(@PathVariable int bookingId,@PathVariable int userId) {
		userService.cancelBooking(bookingId,userId);
	}
	
//	@PostMapping(
//		    value = "/upload-documents"
//		)
//		public UserDocuments uploadDocuments(
//
//		        @RequestParam("userId") int userId,
//		        
//		        @RequestParam("aadhharCardNo") String aadhaarNumber,
//		        @RequestParam("drivingLicenseNumber") String drivingLicenseNumber,
//
//		        @RequestPart("drivingLicense")
//		        MultipartFile drivingLicense,
//
//		        @RequestPart("aadhharCard")
//		        MultipartFile aadhharCard
//		) {
//		System.out.println("aadhharCardNo :"+aadhaarNumber);
//	    System.out.println("drivingLicenseNo : "+drivingLicenseNumber);
//
//		    return userService.uploadDocuments(
//		            userId,
//		            drivingLicense,
//		            aadhharCard,aadhaarNumber,drivingLicenseNumber
//		    );
//		}
//	
	
	@PostMapping(
		    value = "/upload-documents",
		    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
		)
		public UserDocuments uploadDocuments(

		        @RequestParam("userId")
		        int userId,
		        
		        @RequestParam("documentType")
		        String documentType,

		        @RequestParam("drivingLicenseNumber")
		        String drivingLicenseNumber,
		        
		        @RequestParam("licenseExpiry")
		        LocalDate licenseExpiry,

		        @RequestParam("aadhaarNumber")
		        String aadhaarNumber,

		        @RequestPart("drivingLicense")
		        MultipartFile drivingLicense,

		        @RequestPart("aadhharCard")
		        MultipartFile aadhharCard
		) {

		    System.out.println("User ID: " + userId);
		    System.out.println(
		        "Driving Licence Number: " + drivingLicenseNumber
		    );
		    System.out.println(
		        "Aadhaar Number: " + aadhaarNumber
		    );
		    System.out.println(
		        "Driving Licence File: " +
		        drivingLicense.getOriginalFilename()
		    );
		    System.out.println(
		        "Aadhaar Card File: " +
		        aadhharCard.getOriginalFilename()
		    );

		    return userService.uploadDocuments(
		        userId,
		        documentType,
		        drivingLicense,
		        licenseExpiry,
		        aadhharCard,
		        aadhaarNumber,
		        drivingLicenseNumber
		    );
		}
	
	@PutMapping(
	        value = "/update-document/{userId}/{documentType}",
	        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public UserDocuments updateDocument(

	        @PathVariable("userId")
	        int userId,

	        @PathVariable("documentType")
	        String documentType,

	        @RequestParam(
	                value = "drivingLicenseNumber",
	                required = false
	        )
	        String drivingLicenseNumber,

	        @RequestParam(
	                value = "licenseExpiry",
	                required = false
	        )
	        LocalDate licenseExpiry,

	        @RequestParam(
	                value = "aadhaarNumber",
	                required = false
	        )
	        String aadhaarNumber,

	        @RequestPart(
	                value = "drivingLicense",
	                required = false
	        )
	        MultipartFile drivingLicense,

	        @RequestPart(
	                value = "aadhharCard",
	                required = false
	        )
	        MultipartFile aadhharCard
	) {

	    System.out.println("=================================");
	    System.out.println("UPDATE DOCUMENT API");
	    System.out.println("User ID: " + userId);
	    System.out.println("Document Type: " + documentType);
	    System.out.println("Driving Licence Number: " + drivingLicenseNumber);
	    System.out.println("Aadhaar Number: " + aadhaarNumber);

	    if (drivingLicense != null) {
	        System.out.println(
	                "Driving Licence File: "
	                + drivingLicense.getOriginalFilename()
	        );
	    }

	    if (aadhharCard != null) {
	        System.out.println(
	                "Aadhaar Card File: "
	                + aadhharCard.getOriginalFilename()
	        );
	    }

	    return userService.updateDocument(
	            userId,
	            documentType,
	            drivingLicense,
	            licenseExpiry,
	            aadhharCard,
	            aadhaarNumber,
	            drivingLicenseNumber
	    );
	}

	
	@GetMapping("/document/{fileName:.+}")
	public ResponseEntity<Resource> viewDocument(@PathVariable String fileName) {

	    try {

	        String uploadDir =
	                "D:/Nilesh Patil/car-rental-app-main/car-rental-app-main/upload";

	        Path filePath = Paths.get(uploadDir)
	                             .resolve(fileName)
	                             .normalize();

	        Resource resource = new UrlResource(filePath.toUri());

	        if (!resource.exists() || !resource.isReadable()) {
	            return ResponseEntity.notFound().build();
	        }

	        String contentType = Files.probeContentType(filePath);

	        if (contentType == null) {

	            if (fileName.toLowerCase().endsWith(".pdf")) {
	                contentType = "application/pdf";
	            } else if (fileName.toLowerCase().endsWith(".jpg")
	                    || fileName.toLowerCase().endsWith(".jpeg")) {
	                contentType = "image/jpeg";
	            } else if (fileName.toLowerCase().endsWith(".png")) {
	                contentType = "image/png";
	            } else {
	                contentType = "application/octet-stream";
	            }
	        }

	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION,
	                        "inline; filename=\"" + resource.getFilename() + "\"")
	                .contentType(MediaType.parseMediaType(contentType))
	                .body(resource);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.notFound().build();
	    }
	}
	

	
//	@GetMapping("/documents/{userId}")
//	public ResponseEntity<Map<String, String>> getDocumentsByUser(@PathVariable int userId) {
//
//	    try {
//	        String uploadDir =
//	                "D:/Nilesh Patil/car-rental-app-main/car-rental-app-main/upload";
//
//	        UserDocuments documents = userService.getDocumentsByUser(userId);
//
//	        if (documents == null) {
//	            return ResponseEntity.notFound().build();
//	        }
//
//	        Map<String, String> response = new HashMap<>();
//
//	        // Driving License check
//	        String drivingFileName = documents.getDrivingLicense();
//
//	        if (drivingFileName != null) {
//	            Path drivingPath = Paths.get(uploadDir)
//	                    .resolve(drivingFileName)
//	                    .normalize();
//
//	            if (Files.exists(drivingPath)) {
//	                String drivingUrl =
//	                        "http://localhost:8080/user/document/" +
//	                        URLEncoder.encode(drivingFileName, StandardCharsets.UTF_8)
//	                                .replace("+", "%20");
//
//	                response.put("drivingLicense", drivingUrl);
//	            }
//	        }
//
//	        // Aadhaar check
//	        String aadhaarFileName = documents.getAadhharCard();
//
//	        if (aadhaarFileName != null) {
//	            Path aadhaarPath = Paths.get(uploadDir)
//	                    .resolve(aadhaarFileName)
//	                    .normalize();
//
//	            if (Files.exists(aadhaarPath)) {
//	                String aadhaarUrl =
//	                        "http://localhost:8080/user/document/" +
//	                        URLEncoder.encode(aadhaarFileName, StandardCharsets.UTF_8)
//	                                .replace("+", "%20");
//
//	                response.put("aadhharCard", aadhaarUrl);
//	            }
//	        }
//
//	        return ResponseEntity.ok(response);
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return ResponseEntity.internalServerError().build();
//	    }
//	}
	@GetMapping("/documents/{userId}")
	public ResponseEntity<UserDocuments> getDocumentsByUser(
	        @PathVariable int userId) {

	    try {
	        String uploadDir =
	                "D:/Nilesh Patil/car-rental-app-main/car-rental-app-main/upload";

	        UserDocuments documents =
	                userService.getDocumentsByUser(userId);

	        if (documents == null) {
	            return ResponseEntity.notFound().build();
	        }

	        UserDocuments dto = new UserDocuments();

	        // Document database fields
	        dto.setId(documents.getId());
	        dto.setStatus(documents.getStatus());
	        dto.setReason(documents.getReason());
	        dto.setAadharNo(documents.getAadharNo());
	        dto.setDrivingLicenseNo(documents.getDrivingLicenseNo());
	        dto.setLicenseExpiry(documents.getLicenseExpiry());
	        /*
	         * Driving licence URL
	         */
	        String drivingFileName = documents.getDrivingLicense();

	        if (drivingFileName != null && !drivingFileName.isBlank()) {

	            Path drivingPath = Paths.get(uploadDir)
	                    .resolve(drivingFileName)
	                    .normalize();

	            if (Files.exists(drivingPath)) {

	                String drivingUrl =
	                        "http://localhost:8080/user/document/"
	                                + URLEncoder.encode(
	                                        drivingFileName,
	                                        StandardCharsets.UTF_8
	                                ).replace("+", "%20");

	                dto.setDrivingLicense(drivingUrl);
	            }
	        }

	        /*
	         * Aadhaar card URL
	         */
	        String aadhaarFileName = documents.getAadhharCard();

	        if (aadhaarFileName != null && !aadhaarFileName.isBlank()) {

	            Path aadhaarPath = Paths.get(uploadDir)
	                    .resolve(aadhaarFileName)
	                    .normalize();

	            if (Files.exists(aadhaarPath)) {

	                String aadhaarUrl =
	                        "http://localhost:8080/user/document/"
	                                + URLEncoder.encode(
	                                        aadhaarFileName,
	                                        StandardCharsets.UTF_8
	                                ).replace("+", "%20");

	                dto.setAadhharCard(aadhaarUrl);
	            }
	        }

	        /*
	         * User information
	         */
	        if (documents.getUser() != null) {

	            UserRequest user = documents.getUser();
	            
	            dto.setUser(user);
	        }

	        return ResponseEntity.ok(dto);

	    } catch (Exception e) {
	        e.printStackTrace();

	        return ResponseEntity
	                .internalServerError()
	                .build();
	    }
	}
	
	@GetMapping("/listOfDocuments")
	public List<UserDocuments> DocumentList() {
		return userService.DocumentList();
	}
	
	
	@DeleteMapping("/deleteUserDocument/{userId}/{documentType}")
	public void deleteUserDocument(@PathVariable int userId,@PathVariable String documentType) {
		 userService.deleteUserDocument(userId,documentType);
	}
	
	
}
