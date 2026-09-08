//package com.My_Car_Rental_Application.controller;
//
//import com.My_Car_Rental_Application.dto.ApiResponse;
//import com.My_Car_Rental_Application.dto.WhatsAppBookingRequest;
//import com.My_Car_Rental_Application.service.WhatsAppWebhookService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/whatsapp")
//public class WhatsAppWebhookController {
//
//    @Autowired
//    private WhatsAppWebhookService whatsAppWebhookService;
//    
//    @Value("${whatsapp.verify.token:your_verify_token_here}")
//    private String verifyToken;
//
//    // ==========================================
//    // WEBHOOK VERIFICATION (Meta/WhatsApp)
//    // ==========================================
//    @GetMapping("/webhook")
//    public ResponseEntity<String> verifyWebhook(
//            @RequestParam(value = "hub.mode", required = false) String mode,
//            @RequestParam(value = "hub.challenge", required = false) String challenge,
//            @RequestParam(value = "hub.verify_token", required = false) String token) {
//        
//        System.out.println("Webhook Verification Request:");
//        System.out.println("Mode: " + mode);
//        System.out.println("Challenge: " + challenge);
//        System.out.println("Token: " + token);
//        
//        try {
//            if ("subscribe".equals(mode) && verifyToken.equals(token)) {
//                return ResponseEntity.ok(challenge);
//            }
//            
//            return ResponseEntity
//                    .status(HttpStatus.FORBIDDEN)
//                    .body("Verification failed - Invalid token");
//                    
//        } catch (Exception e) {
//            System.err.println("Webhook verification error: " + e.getMessage());
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Verification error: " + e.getMessage());
//        }
//    }
//    
//    @PostMapping("/whatsapp-booking")
//    public ResponseEntity<?> whatsappBooking(
//            @RequestBody WhatsAppBookingRequest request) {
//
//        try {
//
//            Map<String, Object> response =
//                    whatsAppWebhookService.createWhatsAppBooking(request);
//
//            return ResponseEntity.ok(response);
//
//        } catch (RuntimeException e) {
//
//            return ResponseEntity
//                    .badRequest()
//                    .body(Map.of(
//                            "message",
//                            e.getMessage()
//                    ));
//        }
//    }
//
//    // ==========================================
//    // WEBHOOK EVENT HANDLER
//    // ==========================================
//    @PostMapping("/webhook")
//    public ResponseEntity<ApiResponse> handleWebhook(@RequestBody Map<String, Object> payload) {
//        System.out.println("WhatsApp Webhook Received:");
//        System.out.println("Payload: " + payload);
//        
//        try {
//            // Process the webhook payload
//            ApiResponse response = whatsAppWebhookService.processWebhook(payload);
//            
//            if (response.isSuccess()) {
//                return ResponseEntity.ok(response);
//            } else {
//                return ResponseEntity
//                        .status(HttpStatus.BAD_REQUEST)
//                        .body(response);
//            }
//            
//        } catch (Exception e) {
//            System.err.println("Error processing webhook: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponse.error("Error processing webhook: " + e.getMessage()));
//        }
//    }
//
//    // ==========================================
//    // SEND BOOKING CONFIRMATION VIA WHATSAPP
//    // ==========================================
//    @PostMapping("/send-booking-confirmation")
//    public ResponseEntity<ApiResponse> sendBookingConfirmation(
//            @RequestParam Integer bookingId) {
//        
//        try {
//            boolean sent = whatsAppWebhookService.sendBookingConfirmation(bookingId);
//            
//            if (sent) {
//                return ResponseEntity.ok(ApiResponse.success("Booking confirmation sent successfully"));
//            } else {
//                return ResponseEntity
//                        .status(HttpStatus.BAD_REQUEST)
//                        .body(ApiResponse.error("Failed to send booking confirmation"));
//            }
//            
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponse.error("Error: " + e.getMessage()));
//        }
//    }
//
//    // ==========================================
//    // SEND BOOKING STATUS UPDATE VIA WHATSAPP
//    // ==========================================
//    @PostMapping("/send-status-update")
//    public ResponseEntity<ApiResponse> sendStatusUpdate(
//            @RequestParam Integer bookingId,
//            @RequestParam String status) {
//        
//        try {
//            boolean sent = whatsAppWebhookService.sendStatusUpdate(bookingId, status);
//            
//            if (sent) {
//                return ResponseEntity.ok(ApiResponse.success("Status update sent successfully"));
//            } else {
//                return ResponseEntity
//                        .status(HttpStatus.BAD_REQUEST)
//                        .body(ApiResponse.error("Failed to send status update"));
//            }
//            
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponse.error("Error: " + e.getMessage()));
//        }
//    }
//
//    // ==========================================
//    // SEND MANUAL WHATSAPP MESSAGE
//    // ==========================================
//    @PostMapping("/send-message")
//    public ResponseEntity<ApiResponse> sendMessage(
//            @RequestParam String phoneNumber,
//            @RequestParam String message) {
//        
//        try {
//            boolean sent = whatsAppWebhookService.sendMessage(phoneNumber, message);
//            
//            if (sent) {
//                return ResponseEntity.ok(ApiResponse.success("Message sent successfully"));
//            } else {
//                return ResponseEntity
//                        .status(HttpStatus.BAD_REQUEST)
//                        .body(ApiResponse.error("Failed to send message"));
//            }
//            
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponse.error("Error: " + e.getMessage()));
//        }
//    }
//
//    // ==========================================
//    // GET WEBHOOK STATUS
//    // ==========================================
//    @GetMapping("/status")
//    public ResponseEntity<ApiResponse> getWebhookStatus() {
//        try {
//            String status = whatsAppWebhookService.getWebhookStatus();
//            return ResponseEntity.ok(ApiResponse.success(status));
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponse.error("Error getting status: " + e.getMessage()));
//        }
//    }
//}
package com.My_Car_Rental_Application.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.My_Car_Rental_Application.dto.ApiResponse;
import com.My_Car_Rental_Application.dto.WhatsAppBookingRequest;
import com.My_Car_Rental_Application.service.WhatsAppWebhookService;

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppWebhookController {

    @Autowired
    private WhatsAppWebhookService whatsAppWebhookService;


    @Value("${whatsapp.verify.token:your_verify_token_here}")
    private String verifyToken;


    // =====================================================
    // META WEBHOOK VERIFICATION
    // =====================================================

    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(

            @RequestParam(
                    value = "hub.mode",
                    required = false)
            String mode,

            @RequestParam(
                    value = "hub.challenge",
                    required = false)
            String challenge,

            @RequestParam(
                    value = "hub.verify_token",
                    required = false)
            String token) {


        System.out.println(
                "Webhook Verification Request");

        System.out.println(
                "Mode: " + mode);

        System.out.println(
                "Challenge: " + challenge);

        System.out.println(
                "Token: " + token);


        if ("subscribe".equals(mode)
                && verifyToken.equals(token)) {

            System.out.println(
                    "✅ WhatsApp Webhook Verified");

            return ResponseEntity.ok(
                    challenge);
        }


        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        "Verification failed - Invalid token");
    }


    // =====================================================
    // CREATE WHATSAPP BOOKING
    // =====================================================

    @PostMapping("/whatsapp-booking")
    public ResponseEntity<?> whatsappBooking(
            @RequestBody WhatsAppBookingRequest request) {

        try {

            Map<String, Object> response =
                    whatsAppWebhookService
                            .createWhatsAppBooking(
                                    request);

            return ResponseEntity.ok(
                    response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    e.getMessage()
                            ));
        }
    }


    // =====================================================
    // META WEBHOOK EVENT
    // =====================================================

    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse> handleWebhook(

            @RequestBody
            Map<String, Object> payload) {


        System.out.println(
                "WhatsApp Webhook Received");

        System.out.println(
                "Payload: " + payload);


        try {

            ApiResponse response =
                    whatsAppWebhookService
                            .processWebhook(
                                    payload);


            if (response.isSuccess()) {

                return ResponseEntity.ok(
                        response);
            }


            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);


        } catch (Exception e) {

            e.printStackTrace();


            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR)

                    .body(
                            ApiResponse.error(
                                    "Error processing webhook: "
                                            + e.getMessage()));
        }
    }


    // =====================================================
    // SEND BOOKING CONFIRMATION
    // =====================================================

    @PostMapping("/send-booking-confirmation")
    public ResponseEntity<ApiResponse>
    sendBookingConfirmation(

            @RequestParam
            Integer bookingId) {

        try {

            boolean sent =
                    whatsAppWebhookService
                            .sendBookingConfirmation(
                                    bookingId);


            if (sent) {

                return ResponseEntity.ok(
                        ApiResponse.success(
                                "Booking confirmation sent successfully"));
            }


            return ResponseEntity
                    .badRequest()
                    .body(
                            ApiResponse.error(
                                    "Failed to send booking confirmation"));


        } catch (Exception e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR)

                    .body(
                            ApiResponse.error(
                                    e.getMessage()));
        }
    }


    // =====================================================
    // SEND STATUS UPDATE
    // =====================================================

    @PostMapping("/send-status-update")
    public ResponseEntity<ApiResponse>
    sendStatusUpdate(

            @RequestParam
            Integer bookingId,

            @RequestParam
            String status) {

        try {

            boolean sent =
                    whatsAppWebhookService
                            .sendStatusUpdate(
                                    bookingId,
                                    status);


            if (sent) {

                return ResponseEntity.ok(
                        ApiResponse.success(
                                "Status update sent successfully"));
            }


            return ResponseEntity
                    .badRequest()
                    .body(
                            ApiResponse.error(
                                    "Failed to send status update"));


        } catch (Exception e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR)

                    .body(
                            ApiResponse.error(
                                    e.getMessage()));
        }
    }


    // =====================================================
    // SEND MANUAL MESSAGE
    // =====================================================

    @PostMapping("/send-message")
    public ResponseEntity<ApiResponse>
    sendMessage(

            @RequestParam
            String phoneNumber,

            @RequestParam
            String message) {

        try {

            boolean sent =
                    whatsAppWebhookService
                            .sendMessage(
                                    phoneNumber,
                                    message);


            if (sent) {

                return ResponseEntity.ok(
                        ApiResponse.success(
                                "Message sent successfully"));
            }


            return ResponseEntity
                    .badRequest()
                    .body(
                            ApiResponse.error(
                                    "Failed to send message"));


        } catch (Exception e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR)

                    .body(
                            ApiResponse.error(
                                    e.getMessage()));
        }
    }


    // =====================================================
    // GET WHATSAPP STATUS
    // =====================================================

    @GetMapping("/status")
    public ResponseEntity<ApiResponse>
    getWebhookStatus() {

        try {

            String status =
                    whatsAppWebhookService
                            .getWebhookStatus();


            return ResponseEntity.ok(
                    ApiResponse.success(
                            status));

        } catch (Exception e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR)

                    .body(
                            ApiResponse.error(
                                    e.getMessage()));
        }
    }
}