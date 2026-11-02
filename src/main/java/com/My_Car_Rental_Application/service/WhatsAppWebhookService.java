////package com.My_Car_Rental_Application.service;
////
////import java.time.LocalDateTime;
////import java.time.format.DateTimeFormatter;
////import java.util.HashMap;
////import java.util.Map;
////import java.util.Optional;
////
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.http.HttpEntity;
////import org.springframework.http.HttpHeaders;
////import org.springframework.http.HttpMethod;
////import org.springframework.http.HttpStatus;
////import org.springframework.http.MediaType;
////import org.springframework.http.ResponseEntity;
////import org.springframework.stereotype.Service;
////import org.springframework.web.client.RestTemplate;
////
////import com.My_Car_Rental_Application.dto.ApiResponse;
////import com.My_Car_Rental_Application.dto.WhatsAppBookingRequest;
////import com.My_Car_Rental_Application.entity.AdminCarsData;
////import com.My_Car_Rental_Application.entity.Booking;
////import com.My_Car_Rental_Application.entity.UserRequest;
////import com.My_Car_Rental_Application.repository.BookingRepository;
////import com.My_Car_Rental_Application.repository.CarsDataRepository;
////import com.My_Car_Rental_Application.repository.HomeRepository;
////import com.fasterxml.jackson.databind.JsonNode;
////import com.fasterxml.jackson.databind.ObjectMapper;
////
////import jakarta.transaction.Transactional;
////
////@Service
////public class WhatsAppWebhookService {
////
////    @Autowired
////    private BookingRepository bookingRepository;
////    
////    @Autowired
////    private HomeRepository homeRepository;
////    
////    @Autowired
////    private CarsDataRepository carsDataRepository;
////    
////    @Autowired(required = false) // Make it optional to avoid errors if not exists
////    private NotificationService notificationService;
////    
////    @Value("${whatsapp.api.url:https://graph.facebook.com/v18.0}")
////    private String whatsAppApiUrl;
////    
////    @Value("${whatsapp.phone.number.id:}")
////    private String phoneNumberId;
////    
////    @Value("${whatsapp.access.token:}")
////    private String accessToken;
////    
////    @Value("${whatsapp.verify.token:your_verify_token_here}")
////    private String verifyToken;
////    
////    @Value("${whatsapp.admin.number:}")
////    private String adminWhatsAppNumber;
////    
////    private final RestTemplate restTemplate = new RestTemplate();
////    private final ObjectMapper objectMapper = new ObjectMapper();
////
////    // ==========================================
////    // CHECK IF WEBHOOK IS CONFIGURED
////    // ==========================================
////    private boolean isWebhookConfigured() {
////        return phoneNumberId != null && !phoneNumberId.isEmpty() 
////                && accessToken != null && !accessToken.isEmpty()
////                && !phoneNumberId.equals("your_phone_number_id_here")
////                && !accessToken.equals("your_access_token_here");
////    }
////    
////    @Transactional
////    public Map<String, Object> createWhatsAppBooking(
////            WhatsAppBookingRequest request) {
////
////        UserRequest user = homeRepository
////                .findById(request.getUserId())
////                .orElseThrow(() ->
////                        new RuntimeException("User not found"));
////
////        AdminCarsData car = carsDataRepository
////                .findById(request.getCarId())
////                .orElseThrow(() ->
////                        new RuntimeException("Car not found"));
////
////        // Validate dates
////        if (request.getDropoffDate()
////                .isBefore(request.getPickupDate())) {
////
////            throw new RuntimeException(
////                    "Dropoff date cannot be before pickup date");
////        }
////
////        // Create booking
////        Booking booking = new Booking();
////
////        booking.setUser(user);
////        booking.setAdminCarsData(car);
////
////        booking.setPickupDate(request.getPickupDate());
////        booking.setDropoffDate(request.getDropoffDate());
////
////        booking.setPickupLocation(
////                request.getPickupLocation());
////
////        booking.setDropoffLocation(
////                request.getDropoffLocation());
////
////        booking.setTripDriverType(
////                request.getTripDriverType());
////
////        booking.setDays(request.getDays());
////
////        booking.setPrice(request.getPrice());
////
////        booking.setTotal(request.getTotal());
////
////        booking.setBookingStatus(
////                "WHATSAPP_PENDING");
////
////        booking.setBookingSource(
////                "WHATSAPP");
////
////        Booking savedBooking =
////                bookingRepository.save(booking);
////
////        // Now create WhatsApp message
////        String message =
////                createAdminBookingMessage(savedBooking);
////
////        // Send message to admin
////        boolean sent =
////                sendWhatsAppMessage(
////                		adminWhatsAppNumber,
////                        message
////                );
////
////        if (!sent) {
////            throw new RuntimeException(
////                    "Booking created but WhatsApp message could not be sent");
////        }
////
////        return Map.of(
////                "success", true,
////                "bookingId", savedBooking.getId(),
////                "status", savedBooking.getBookingStatus(),
////                "message",
////                "Booking request sent to admin on WhatsApp"
////        );
////    }
////    
////    private String generateBookingMessage(
////            Booking booking,
////            UserRequest user,
////            AdminCarsData car,
////            double insurance,
////            double tax) {
////
////        return """
////                🚗 *NEW CAR BOOKING REQUEST*
////
////                ━━━━━━━━━━━━━━━━━━━━
////                📋 *BOOKING ID*
////                ━━━━━━━━━━━━━━━━━━━━
////
////                Booking ID: #%d
////
////                ━━━━━━━━━━━━━━━━━━━━
////                🚘 *CAR DETAILS*
////                ━━━━━━━━━━━━━━━━━━━━
////
////                Car: %s
////                Brand: %s
////                Daily Rate: ₹%,.0f
////
////                ━━━━━━━━━━━━━━━━━━━━
////                📅 *TRIP DETAILS*
////                ━━━━━━━━━━━━━━━━━━━━
////
////                Pickup Date: %s
////                Dropoff Date: %s
////                Pickup Location: %s
////                Dropoff Location: %s
////                Trip Type: %s
////
////                ━━━━━━━━━━━━━━━━━━━━
////                👤 *CUSTOMER DETAILS*
////                ━━━━━━━━━━━━━━━━━━━━
////
////                Name: %s
////                Email: %s
////                Phone: %s
////
////                ━━━━━━━━━━━━━━━━━━━━
////                💰 *BOOKING SUMMARY*
////                ━━━━━━━━━━━━━━━━━━━━
////
////                Rental Days: %d
////                Daily Rate: ₹%,.0f
////                Subtotal: ₹%,.0f
////                Insurance: ₹%,.0f
////                Tax: ₹%,.0f
////
////                *TOTAL: ₹%,.0f*
////
////                ━━━━━━━━━━━━━━━━━━━━
////
////                Please confirm my car booking.
////
////                Reply:
////
////                CONFIRM %d
////
////                or
////
////                REJECT %d
////                """.formatted(
////                        booking.getId(),
////                        car.getName(),
////                        car.getBrand(),
////                        booking.getPrice(),
////
////                        booking.getPickupDate(),
////                        booking.getDropoffDate(),
////                        booking.getPickupLocation(),
////                        booking.getDropoffLocation(),
////                        booking.getTripDriverType(),
////
////                        user.getFirstName() + " " + user.getLastName(),
////                        user.getEmail(),
////                        user.getPhone(),
////
////                        booking.getDays(),
////                        booking.getPrice(),
////                        booking.getPrice() * booking.getDays(),
////                        insurance,
////                        tax,
////                        booking.getTotal(),
////
////                        booking.getId(),
////                        booking.getId()
////                );
////    }
////    
////    private String createAdminBookingMessage(
////            Booking booking) {
////
////        UserRequest user = booking.getUser();
////        AdminCarsData car = booking.getAdminCarsData();
////
////        return """
////                🚗 *NEW CAR BOOKING REQUEST*
////
////                ━━━━━━━━━━━━━━━━━━━━
////                🚘 *CAR DETAILS*
////                ━━━━━━━━━━━━━━━━━━━━
////
////                Booking ID: %d
////                Car: %s
////                Brand: %s
////                Daily Rate: ₹%.0f
////
////                ━━━━━━━━━━━━━━━━━━━━
////                📅 *TRIP DETAILS*
////                ━━━━━━━━━━━━━━━━━━━━
////
////                Pickup Date: %s
////                Dropoff Date: %s
////                Pickup Location: %s
////                Dropoff Location: %s
////                Trip Type: %s
////
////                ━━━━━━━━━━━━━━━━━━━━
////                👤 *CUSTOMER DETAILS*
////                ━━━━━━━━━━━━━━━━━━━━
////
////                Name: %s %s
////                Email: %s
////                Phone: %s
////
////                ━━━━━━━━━━━━━━━━━━━━
////                💰 *BOOKING SUMMARY*
////                ━━━━━━━━━━━━━━━━━━━━
////
////                Rental Days: %d
////                Daily Rate: ₹%.0f
////                Total: ₹%.0f
////
////                ━━━━━━━━━━━━━━━━━━━━
////
////                Reply:
////                CONFIRM %d
////
////                or
////
////                REJECT %d
////
////                Thank you.
////                """.formatted(
////
////                booking.getId(),
////
////                car.getName(),
////                car.getBrand(),
////                booking.getPrice(),
////
////                booking.getPickupDate(),
////                booking.getDropoffDate(),
////
////                booking.getPickupLocation(),
////                booking.getDropoffLocation(),
////
////                booking.getTripDriverType(),
////
////                user.getFirstName(),
////                user.getLastName(),
////
////                user.getEmail(),
////                user.getPhone(),
////
////                booking.getDays(),
////                booking.getPrice(),
////                booking.getTotal(),
////
////                booking.getId(),
////                booking.getId()
////        );
////    }
////
////    // ==========================================
////    // VALIDATE WHATSAPP PAYLOAD
////    // ==========================================
////    private boolean isValidWhatsAppPayload(Map<String, Object> payload) {
////        return payload != null && (payload.containsKey("entry") || payload.containsKey("object"));
////    }
////
////    // ==========================================
////    // GET WEBHOOK STATUS
////    // ==========================================
////    public String getWebhookStatus() {
////        try {
////            Map<String, Object> status = new HashMap<>();
////            status.put("configured", isWebhookConfigured());
////            status.put("phoneNumberId", phoneNumberId != null && !phoneNumberId.isEmpty() ? "SET" : "NOT SET");
////            status.put("accessToken", accessToken != null && !accessToken.isEmpty() ? "SET" : "NOT SET");
////            status.put("verifyToken", verifyToken != null && !verifyToken.isEmpty() ? "SET" : "NOT SET");
////            status.put("apiUrl", whatsAppApiUrl);
////            status.put("timestamp", LocalDateTime.now().toString());
////            
////            return objectMapper.writeValueAsString(status);
////        } catch (Exception e) {
////            return "{\"error\": \"" + e.getMessage() + "\"}";
////        }
////    }
////
////    // ==========================================
////    // PROCESS WEBHOOK PAYLOAD
////    // ==========================================
////    public ApiResponse processWebhook(Map<String, Object> payload) {
////        try {
////            System.out.println("Processing webhook payload...");
////            
////            // Check if webhook is configured
////            if (!isWebhookConfigured()) {
////                return ApiResponse.error("Webhook not properly configured. Please check your WhatsApp API credentials.");
////            }
////            
////            // Check if payload is from WhatsApp
////            if (!isValidWhatsAppPayload(payload)) {
////                return ApiResponse.error("Invalid WhatsApp webhook payload");
////            }
////            
////            String payloadJson = objectMapper.writeValueAsString(payload);
////            JsonNode root = objectMapper.readTree(payloadJson);
////            
////            // Check for messages
////            if (hasMessages(root)) {
////                processMessages(root);
////                return ApiResponse.success("Messages processed successfully");
////            }
////            
////            // Check for status updates
////            if (hasStatusUpdates(root)) {
////                processStatusUpdates(root);
////                return ApiResponse.success("Status updates processed successfully");
////            }
////            
////            // Check if it's a direct booking request
////            if (root.has("bookingId") && root.has("status")) {
////                Integer bookingId = root.path("bookingId").asInt();
////                String status = root.path("status").asText();
////                String message = root.path("message").asText("");
////                
////                return processBookingStatusUpdate(bookingId, status, message);
////            }
////            
////            return ApiResponse.success("Webhook received but no actionable data");
////            
////        } catch (Exception e) {
////            System.err.println("Error processing webhook: " + e.getMessage());
////            e.printStackTrace();
////            return ApiResponse.error("Error processing webhook: " + e.getMessage());
////        }
////    }
////
////    // ==========================================
////    // CHECK FOR MESSAGES
////    // ==========================================
////    private boolean hasMessages(JsonNode root) {
////        if (root.has("entry")) {
////            JsonNode entry = root.path("entry");
////            for (JsonNode entryNode : entry) {
////                if (entryNode.has("changes")) {
////                    JsonNode changes = entryNode.path("changes");
////                    for (JsonNode change : changes) {
////                        if ("messages".equals(change.path("field").asText())) {
////                            return true;
////                        }
////                    }
////                }
////            }
////        }
////        return false;
////    }
////
////    // ==========================================
////    // CHECK FOR STATUS UPDATES
////    // ==========================================
////    private boolean hasStatusUpdates(JsonNode root) {
////        if (root.has("entry")) {
////            JsonNode entry = root.path("entry");
////            for (JsonNode entryNode : entry) {
////                if (entryNode.has("changes")) {
////                    JsonNode changes = entryNode.path("changes");
////                    for (JsonNode change : changes) {
////                        if ("statuses".equals(change.path("field").asText())) {
////                            return true;
////                        }
////                    }
////                }
////            }
////        }
////        return false;
////    }
////
////    // ==========================================
////    // PROCESS MESSAGES
////    // ==========================================
////    private void processMessages(JsonNode root) throws Exception {
////        JsonNode entry = root.path("entry");
////        for (JsonNode entryNode : entry) {
////            JsonNode changes = entryNode.path("changes");
////            for (JsonNode change : changes) {
////                JsonNode value = change.path("value");
////                if (value.has("messages")) {
////                    JsonNode messages = value.path("messages");
////                    for (JsonNode message : messages) {
////                        processIncomingMessage(message);
////                    }
////                }
////            }
////        }
////    }
////
////    // ==========================================
////    // PROCESS STATUS UPDATES
////    // ==========================================
////    private void processStatusUpdates(JsonNode root) throws Exception {
////        JsonNode entry = root.path("entry");
////        for (JsonNode entryNode : entry) {
////            JsonNode changes = entryNode.path("changes");
////            for (JsonNode change : changes) {
////                JsonNode value = change.path("value");
////                if (value.has("statuses")) {
////                    JsonNode statuses = value.path("statuses");
////                    for (JsonNode status : statuses) {
////                        processMessageStatus(status);
////                    }
////                }
////            }
////        }
////    }
////
////    // ==========================================
////    // PROCESS INDIVIDUAL CHANGE
////    // ==========================================
////    private void processChange(JsonNode change) {
////        try {
////            String field = change.path("field").asText();
////            
////            if ("messages".equals(field)) {
////                JsonNode value = change.path("value");
////                if (value.has("messages")) {
////                    JsonNode messages = value.path("messages");
////                    for (JsonNode message : messages) {
////                        processIncomingMessage(message);
////                    }
////                }
////            }
////            
////            if ("statuses".equals(field)) {
////                JsonNode value = change.path("value");
////                if (value.has("statuses")) {
////                    JsonNode statuses = value.path("statuses");
////                    for (JsonNode status : statuses) {
////                        processMessageStatus(status);
////                    }
////                }
////            }
////            
////        } catch (Exception e) {
////            System.err.println("Error processing change: " + e.getMessage());
////        }
////    }
////
////    // ==========================================
////    // PROCESS INCOMING MESSAGE
////    // ==========================================
////    private void processIncomingMessage(JsonNode message) {
////        try {
////            String from = message.path("from").asText();
////            String text = message.path("text").path("body").asText();
////            String messageId = message.path("id").asText();
////            
////            System.out.println("📩 Message from: " + from);
////            System.out.println("📝 Message text: " + text);
////            System.out.println("🆔 Message ID: " + messageId);
////            
////            // Auto-reply based on message content
////            String reply = generateAutoReply(text);
////            
////            if (reply != null && !reply.isEmpty()) {
////                sendMessage(from, reply);
////                System.out.println("📤 Auto-reply sent: " + reply);
////            }
////            
////        } catch (Exception e) {
////            System.err.println("Error processing incoming message: " + e.getMessage());
////        }
////    }
////
////    // ==========================================
////    // PROCESS MESSAGE STATUS
////    // ==========================================
////    private void processMessageStatus(JsonNode status) {
////        try {
////            String statusType = status.path("status").asText();
////            String recipientId = status.path("recipient_id").asText();
////            String messageId = status.path("id").asText();
////            
////            System.out.println("📊 Message Status: " + statusType);
////            System.out.println("👤 Recipient: " + recipientId);
////            System.out.println("🆔 Message ID: " + messageId);
////            
////        } catch (Exception e) {
////            System.err.println("Error processing message status: " + e.getMessage());
////        }
////    }
////
////    // ==========================================
////    // GENERATE AUTO-REPLY
////    // ==========================================
////    private String generateAutoReply(String message) {
////        String lowerMsg = message.toLowerCase().trim();
////        
////        if (lowerMsg.contains("book") || lowerMsg.contains("booking") || lowerMsg.contains("reserve")) {
////            return "Thank you for your interest in booking! 🚗\n\n" +
////                   "Please visit our website to complete your booking:\n" +
////                   "https://your-website.com/book\n\n" +
////                   "For quick booking, you can also call us at: +91-XXXXXXXXXX";
////        }
////        
////        if (lowerMsg.contains("status") || lowerMsg.contains("update")) {
////            return "To check your booking status, please provide your Booking ID.\n\n" +
////                   "Example: Status 123\n\n" +
////                   "Or visit our website to view all your bookings.";
////        }
////        
////        if (lowerMsg.contains("cancel") || lowerMsg.contains("cancellation")) {
////            return "To cancel your booking, please provide your Booking ID.\n\n" +
////                   "Example: Cancel 123\n\n" +
////                   "Note: Cancellation charges may apply as per our policy.";
////        }
////        
////        if (lowerMsg.contains("help") || lowerMsg.contains("support")) {
////            return "How can I help you? 🤝\n\n" +
////                   "• To book a car - type 'Book'\n" +
////                   "• To check status - type 'Status'\n" +
////                   "• To cancel - type 'Cancel'\n" +
////                   "• To speak with support - call: +91-XXXXXXXXXX\n\n" +
////                   "Our team is here to assist you! 💙";
////        }
////        
////        if (lowerMsg.contains("hi") || lowerMsg.contains("hello") || lowerMsg.contains("hey")) {
////            return "Hello! 👋 Welcome to Car Rental Service!\n\n" +
////                   "How can I assist you today?\n" +
////                   "• Book a car 🚗\n" +
////                   "• Check booking status 📋\n" +
////                   "• Cancel booking ❌\n" +
////                   "• Get help 🤝\n\n" +
////                   "Just type what you need!";
////        }
////        
////        return null;
////    }
////
////    // ==========================================
////    // HANDLE BOOKING REQUEST FROM WHATSAPP
////    // ==========================================
////    private void handleBookingRequest(String from, String text) {
////        try {
////            String response = "Thank you for your booking request! " +
////                "Please visit our website to complete your booking: " +
////                "https://your-website.com/book";
////            sendMessage(from, response);
////        } catch (Exception e) {
////            System.err.println("Error handling booking request: " + e.getMessage());
////        }
////    }
////
////    // ==========================================
////    // HANDLE STATUS INQUIRY
////    // ==========================================
////    private void handleStatusInquiry(String from, String text) {
////        try {
////            String bookingIdStr = text.replaceAll("[^0-9]", "");
////            
////            if (!bookingIdStr.isEmpty()) {
////                Integer bookingId = Integer.parseInt(bookingIdStr);
////                Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
////                
////                if (bookingOpt.isPresent()) {
////                    Booking booking = bookingOpt.get();
////                    String status = booking.getBookingStatus();
////                    String response = "Booking #" + bookingId + " is currently: " + status;
////                    sendMessage(from, response);
////                } else {
////                    sendMessage(from, "Booking #" + bookingId + " not found.");
////                }
////            } else {
////                sendMessage(from, "Please provide your booking ID to check status.");
////            }
////            
////        } catch (Exception e) {
////            System.err.println("Error handling status inquiry: " + e.getMessage());
////        }
////    }
////
////    // ==========================================
////    // SEND BOOKING CONFIRMATION
////    // ==========================================
////    public boolean sendBookingConfirmation(Integer bookingId) {
////        try {
////            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
////            
////            if (!bookingOpt.isPresent()) {
////                System.err.println("Booking not found: " + bookingId);
////                return false;
////            }
////            
////            Booking booking = bookingOpt.get();
////            UserRequest user = booking.getUser();
////            
////            String phoneNumber = user.getPhone();
////            if (phoneNumber == null || phoneNumber.isEmpty()) {
////                System.err.println("No phone number for user: " + user.getId());
////                return false;
////            }
////            
////            String formattedPhone = formatPhoneNumber(phoneNumber);
////            String message = buildBookingConfirmationMessage(booking);
////            
////            return sendMessage(formattedPhone, message);
////            
////        } catch (Exception e) {
////            System.err.println("Error sending booking confirmation: " + e.getMessage());
////            return false;
////        }
////    }
////
////    // ==========================================
////    // SEND STATUS UPDATE
////    // ==========================================
////    public boolean sendStatusUpdate(Integer bookingId, String status) {
////        try {
////            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
////            
////            if (!bookingOpt.isPresent()) {
////                System.err.println("Booking not found: " + bookingId);
////                return false;
////            }
////            
////            Booking booking = bookingOpt.get();
////            UserRequest user = booking.getUser();
////            
////            String phoneNumber = user.getPhone();
////            if (phoneNumber == null || phoneNumber.isEmpty()) {
////                System.err.println("No phone number for user: " + user.getId());
////                return false;
////            }
////            
////            String formattedPhone = formatPhoneNumber(phoneNumber);
////            String message = buildStatusUpdateMessage(booking, status);
////            
////            boolean sent = sendMessage(formattedPhone, message);
////            
////            if (sent && notificationService != null) {
////                notificationService.createNotification(
////                    user.getId(),
////                    "Booking Status Updated",
////                    "Your booking #" + bookingId + " is now " + status,
////                    "BOOKING",
////                    bookingId
////                );
////            }
////            
////            return sent;
////            
////        } catch (Exception e) {
////            System.err.println("Error sending status update: " + e.getMessage());
////            return false;
////        }
////    }
////
////    // ==========================================
////    // SEND WHATSAPP MESSAGE
////    // ==========================================
////    public boolean sendMessage(String phoneNumber, String message) {
////        try {
////            String formattedPhone = formatPhoneNumber(phoneNumber);
////            
////            System.out.println("📤 Sending WhatsApp message to: " + formattedPhone);
////            System.out.println("📝 Message: " + message);
////            
////            if (!isWebhookConfigured()) {
////                System.err.println("WhatsApp API not configured. Message would be sent in production.");
////                System.out.println("💬 [DEV] Message: " + message);
////                return true;
////            }
////            
////            Map<String, Object> requestBody = new HashMap<>();
////            requestBody.put("messaging_product", "whatsapp");
////            requestBody.put("to", formattedPhone);
////            requestBody.put("type", "text");
////            
////            Map<String, String> textObj = new HashMap<>();
////            textObj.put("body", message);
////            requestBody.put("text", textObj);
////            
////            HttpHeaders headers = new HttpHeaders();
////            headers.setContentType(MediaType.APPLICATION_JSON);
////            headers.setBearerAuth(accessToken);
////            
////            String url = whatsAppApiUrl + "/" + phoneNumberId + "/messages";
////            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
////            
////            ResponseEntity<String> response = restTemplate.exchange(
////                url,
////                HttpMethod.POST,
////                entity,
////                String.class
////            );
////            
////            if (response.getStatusCode() == HttpStatus.OK || 
////                response.getStatusCode() == HttpStatus.CREATED) {
////                System.out.println("✅ WhatsApp message sent successfully");
////                return true;
////            } else {
////                System.err.println("❌ Failed to send WhatsApp message");
////                System.err.println("Status: " + response.getStatusCode());
////                return false;
////            }
////            
////        } catch (Exception e) {
////            System.err.println("❌ Error sending WhatsApp message: " + e.getMessage());
////            e.printStackTrace();
////            return false;
////        }
////    }
////
////    // ==========================================
////    // PROCESS BOOKING STATUS UPDATE
////    // ==========================================
////    private ApiResponse processBookingStatusUpdate(Integer bookingId, String status, String message) {
////        try {
////            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
////            
////            if (!bookingOpt.isPresent()) {
////                return ApiResponse.error("Booking not found: " + bookingId);
////            }
////            
////            Booking booking = bookingOpt.get();
////            booking.setBookingStatus(status);
////            bookingRepository.save(booking);
////            
////            sendStatusUpdate(bookingId, status);
////            
////            return ApiResponse.success("Booking status updated successfully");
////            
////        } catch (Exception e) {
////            return ApiResponse.error("Error updating booking: " + e.getMessage());
////        }
////    }
////
////    // ==========================================
////    // BUILD BOOKING CONFIRMATION MESSAGE
////    // ==========================================
////    private String buildBookingConfirmationMessage(Booking booking) {
////        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
////        
////        return String.format("""
////            🚗 *BOOKING CONFIRMED!* ✅
////            
////            Booking ID: #%d
////            Car: %s %s
////            Pickup: %s
////            Dropoff: %s
////            Total: ₹%.2f
////            
////            Thank you for choosing our service! 💙
////            """,
////            booking.getId(),
////            booking.getAdminCarsData().getBrand(),
////            booking.getAdminCarsData().getName(),
////            booking.getPickupDate().format(formatter),
////            booking.getDropoffDate().format(formatter),
////            booking.getTotal()
////        );
////    }
////
////    // ==========================================
////    // BUILD STATUS UPDATE MESSAGE
////    // ==========================================
////    private String buildStatusUpdateMessage(Booking booking, String status) {
////        String statusEmoji = getStatusEmoji(status);
////        String statusMessage = getStatusMessage(status);
////        
////        return String.format("""
////            %s *BOOKING STATUS UPDATE*
////            
////            Booking ID: #%d
////            Status: %s
////            Car: %s %s
////            
////            %s
////            
////            Thank you for choosing our service. 💙
////            """,
////            statusEmoji,
////            booking.getId(),
////            status,
////            booking.getAdminCarsData().getBrand(),
////            booking.getAdminCarsData().getName(),
////            statusMessage
////        );
////    }
////
////    // ==========================================
////    // HELPER METHODS
////    // ==========================================
////    
////    private String getStatusEmoji(String status) {
////        if (status == null) return "📋";
////        
////        switch (status.toUpperCase()) {
////            case "CONFIRMED": return "✅";
////            case "REJECTED": return "❌";
////            case "PENDING": return "⏳";
////            case "CANCELLED": return "📅";
////            default: return "📋";
////        }
////    }
////    
////    private String getStatusMessage(String status) {
////        if (status == null) return "Your booking status has been updated.";
////        
////        switch (status.toUpperCase()) {
////            case "CONFIRMED": 
////                return "🎉 Your booking has been confirmed! We look forward to serving you.";
////            case "REJECTED": 
////                return "😔 We regret to inform you that your booking has been rejected.";
////            case "PENDING": 
////                return "⏳ Your booking is still pending review. We'll update you soon.";
////            case "CANCELLED": 
////                return "📅 Your booking has been cancelled as requested.";
////            default: 
////                return "Your booking status has been updated to: " + status;
////        }
////    }
////    
////    private String formatPhoneNumber(String phoneNumber) {
////        if (phoneNumber == null) return "";
////        
////        String cleaned = phoneNumber.replaceAll("[^0-9]", "");
////        
////        if (!cleaned.startsWith("91") && cleaned.length() == 10) {
////            cleaned = "91" + cleaned;
////        }
////        
////        return cleaned;
////    }
////}
//package com.My_Car_Rental_Application.service;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import com.My_Car_Rental_Application.dto.ApiResponse;
//import com.My_Car_Rental_Application.dto.WhatsAppBookingRequest;
//import com.My_Car_Rental_Application.entity.AdminCarsData;
//import com.My_Car_Rental_Application.entity.Booking;
//import com.My_Car_Rental_Application.entity.UserRequest;
//import com.My_Car_Rental_Application.repository.BookingRepository;
//import com.My_Car_Rental_Application.repository.CarsDataRepository;
//import com.My_Car_Rental_Application.repository.HomeRepository;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import jakarta.transaction.Transactional;
//
//@Service
//public class WhatsAppWebhookService {
//
//    @Autowired
//    private BookingRepository bookingRepository;
//
//    @Autowired
//    private HomeRepository homeRepository;
//
//    @Autowired
//    private CarsDataRepository carsDataRepository;
//
//    @Autowired(required = false)
//    private NotificationService notificationService;
//
//    // =====================================================
//    // WHATSAPP CONFIGURATION
//    // =====================================================
//
//    @Value("${whatsapp.api.url:https://graph.facebook.com/v18.0}")
//    private String whatsAppApiUrl;
//
//    /*
//     * This is the WhatsApp Business Phone Number ID
//     * provided by Meta.
//     */
//    @Value("${whatsapp.phone.number.id:}")
//    private String phoneNumberId;
//
//    /*
//     * Meta WhatsApp Cloud API access token.
//     */
//    @Value("${whatsapp.access.token:}")
//    private String accessToken;
//
//    /*
//     * Token used by Meta to verify webhook.
//     */
//    @Value("${whatsapp.verify.token:your_verify_token_here}")
//    private String verifyToken;
//
//    /*
//     * Actual WhatsApp number of ADMIN.
//     *
//     * Example:
//     * 919876543210
//     */
//    @Value("${whatsapp.admin.number:}")
//    private String adminWhatsAppNumber;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//
//    // =====================================================
//    // CHECK WHATSAPP CONFIGURATION
//    // =====================================================
//
//    private boolean isWebhookConfigured() {
//
//        return phoneNumberId != null
//                && !phoneNumberId.trim().isEmpty()
//
//                && accessToken != null
//                && !accessToken.trim().isEmpty()
//
//                && !phoneNumberId.equals("your_phone_number_id_here")
//
//                && !accessToken.equals("your_access_token_here");
//    }
//
//
//    // =====================================================
//    // CREATE WHATSAPP BOOKING
//    // =====================================================
//
//    @Transactional
//    public Map<String, Object> createWhatsAppBooking(
//            WhatsAppBookingRequest request) {
//
//        // -------------------------------------------------
//        // Find User
//        // -------------------------------------------------
//
//        UserRequest user = homeRepository
//                .findById(request.getUserId())
//                .orElseThrow(() ->
//                        new RuntimeException("User not found"));
//
//
//        // -------------------------------------------------
//        // Find Car
//        // -------------------------------------------------
//
//        AdminCarsData car = carsDataRepository
//                .findById(request.getCarId())
//                .orElseThrow(() ->
//                        new RuntimeException("Car not found"));
//
//
//        // -------------------------------------------------
//        // Validate Dates
//        // -------------------------------------------------
//
//        if (request.getDropoffDate()
//                .isBefore(request.getPickupDate())) {
//
//            throw new RuntimeException(
//                    "Dropoff date cannot be before pickup date");
//        }
//
//
//        // -------------------------------------------------
//        // Validate Admin WhatsApp Number
//        // -------------------------------------------------
//
//        if (adminWhatsAppNumber == null
//                || adminWhatsAppNumber.trim().isEmpty()) {
//
//            throw new RuntimeException(
//                    "Admin WhatsApp number is not configured. "
//                    + "Add whatsapp.admin.number in application.properties");
//        }
//
//
//        // -------------------------------------------------
//        // Create Booking
//        // -------------------------------------------------
//
//        Booking booking = new Booking();
//
//        booking.setUser(user);
//
//        booking.setAdminCarsData(car);
//
//        booking.setPickupDate(
//                request.getPickupDate());
//
//        booking.setDropoffDate(
//                request.getDropoffDate());
//
//        booking.setPickupLocation(
//                request.getPickupLocation());
//
//        booking.setDropoffLocation(
//                request.getDropoffLocation());
//
//        booking.setTripDriverType(
//                request.getTripDriverType());
//
//        booking.setDays(
//                request.getDays());
//
//        booking.setPrice(
//                request.getPrice());
//
//        booking.setTotal(
//                request.getTotal());
//
//        booking.setBookingStatus(
//                "WHATSAPP_PENDING");
//
//        booking.setBookingSource(
//                "WHATSAPP");
//
//        booking.setWhatsappStatus(
//                "PENDING");
//
//
//        // -------------------------------------------------
//        // Save Booking
//        // -------------------------------------------------
//
//        Booking savedBooking =
//                bookingRepository.save(booking);
//
//
//        // -------------------------------------------------
//        // Create Admin WhatsApp Message
//        // -------------------------------------------------
//
//        String message =
//                createAdminBookingMessage(savedBooking);
//
//
//        // -------------------------------------------------
//        // Send Message To ADMIN
//        // -------------------------------------------------
//
//        boolean sent =
//                sendMessage(
//                        adminWhatsAppNumber,
//                        message
//                );
//
//
//        // -------------------------------------------------
//        // Update WhatsApp Status
//        // -------------------------------------------------
//
//        if (sent) {
//
//            savedBooking.setWhatsappStatus("SENT");
//
//            bookingRepository.save(savedBooking);
//
//        } else {
//
//            savedBooking.setWhatsappStatus("FAILED");
//
//            bookingRepository.save(savedBooking);
//
//            throw new RuntimeException(
//                    "Booking created but WhatsApp message could not be sent");
//        }
//
//
//        // -------------------------------------------------
//        // Response
//        // -------------------------------------------------
//
//        return Map.of(
//                "success", true,
//                "bookingId", savedBooking.getId(),
//                "status", savedBooking.getBookingStatus(),
//                "whatsappStatus", savedBooking.getWhatsappStatus(),
//                "message",
//                "Booking request sent to admin on WhatsApp"
//        );
//    }
//
//
//    // =====================================================
//    // CREATE ADMIN BOOKING MESSAGE
//    // =====================================================
//
//    private String createAdminBookingMessage(
//            Booking booking) {
//
//        UserRequest user =
//                booking.getUser();
//
//        AdminCarsData car =
//                booking.getAdminCarsData();
//
//        return """
//                🚗 *NEW CAR BOOKING REQUEST*
//
//                ━━━━━━━━━━━━━━━━━━━━
//                🚘 *CAR DETAILS*
//                ━━━━━━━━━━━━━━━━━━━━
//
//                Booking ID: %d
//                Car: %s
//                Brand: %s
//                Daily Rate: ₹%.0f
//
//                ━━━━━━━━━━━━━━━━━━━━
//                📅 *TRIP DETAILS*
//                ━━━━━━━━━━━━━━━━━━━━
//
//                Pickup Date: %s
//                Dropoff Date: %s
//                Pickup Location: %s
//                Dropoff Location: %s
//                Trip Type: %s
//
//                ━━━━━━━━━━━━━━━━━━━━
//                👤 *CUSTOMER DETAILS*
//                ━━━━━━━━━━━━━━━━━━━━
//
//                Name: %s %s
//                Email: %s
//                Phone: %s
//
//                ━━━━━━━━━━━━━━━━━━━━
//                💰 *BOOKING SUMMARY*
//                ━━━━━━━━━━━━━━━━━━━━
//
//                Rental Days: %d
//                Daily Rate: ₹%.0f
//                Total: ₹%.0f
//
//                ━━━━━━━━━━━━━━━━━━━━
//
//                Please select:
//
//                CONFIRM %d
//
//                or
//
//                REJECT %d
//
//                Thank you.
//                """.formatted(
//
//                booking.getId(),
//
//                car.getName(),
//
//                car.getBrand(),
//
//                booking.getPrice(),
//
//                booking.getPickupDate(),
//
//                booking.getDropoffDate(),
//
//                booking.getPickupLocation(),
//
//                booking.getDropoffLocation(),
//
//                booking.getTripDriverType(),
//
//                user.getFirstName(),
//
//                user.getLastName(),
//
//                user.getEmail(),
//
//                user.getPhone(),
//
//                booking.getDays(),
//
//                booking.getPrice(),
//
//                booking.getTotal(),
//
//                booking.getId(),
//
//                booking.getId()
//        );
//    }
//
//
//    // =====================================================
//    // VALIDATE WHATSAPP PAYLOAD
//    // =====================================================
//
//    private boolean isValidWhatsAppPayload(
//            Map<String, Object> payload) {
//
//        return payload != null
//                && (
//                payload.containsKey("entry")
//                        || payload.containsKey("object")
//        );
//    }
//
//
//    // =====================================================
//    // GET WEBHOOK STATUS
//    // =====================================================
//
//    public String getWebhookStatus() {
//
//        try {
//
//            Map<String, Object> status =
//                    new HashMap<>();
//
//            status.put(
//                    "configured",
//                    isWebhookConfigured());
//
//            status.put(
//                    "phoneNumberId",
//                    phoneNumberId != null
//                            && !phoneNumberId.isEmpty()
//                            ? "SET"
//                            : "NOT SET");
//
//            status.put(
//                    "accessToken",
//                    accessToken != null
//                            && !accessToken.isEmpty()
//                            ? "SET"
//                            : "NOT SET");
//
//            status.put(
//                    "verifyToken",
//                    verifyToken != null
//                            && !verifyToken.isEmpty()
//                            ? "SET"
//                            : "NOT SET");
//
//            status.put(
//                    "adminWhatsAppNumber",
//                    adminWhatsAppNumber != null
//                            && !adminWhatsAppNumber.isEmpty()
//                            ? "SET"
//                            : "NOT SET");
//
//            status.put(
//                    "apiUrl",
//                    whatsAppApiUrl);
//
//            status.put(
//                    "timestamp",
//                    LocalDateTime.now().toString());
//
//            return objectMapper.writeValueAsString(status);
//
//        } catch (Exception e) {
//
//            return "{\"error\":\""
//                    + e.getMessage()
//                    + "\"}";
//        }
//    }
//
//
//    // =====================================================
//    // PROCESS WEBHOOK
//    // =====================================================
//
//    public ApiResponse processWebhook(
//            Map<String, Object> payload) {
//
//        try {
//
//            System.out.println(
//                    "Processing WhatsApp webhook payload...");
//
//
//            // -------------------------------------------------
//            // Validate Configuration
//            // -------------------------------------------------
//
//            if (!isWebhookConfigured()) {
//
//                return ApiResponse.error(
//                        "WhatsApp API is not configured");
//            }
//
//
//            // -------------------------------------------------
//            // Validate Payload
//            // -------------------------------------------------
//
//            if (!isValidWhatsAppPayload(payload)) {
//
//                return ApiResponse.error(
//                        "Invalid WhatsApp webhook payload");
//            }
//
//
//            // -------------------------------------------------
//            // Convert JSON
//            // -------------------------------------------------
//
//            String payloadJson =
//                    objectMapper.writeValueAsString(payload);
//
//            JsonNode root =
//                    objectMapper.readTree(payloadJson);
//
//
//            // -------------------------------------------------
//            // Incoming Messages
//            // -------------------------------------------------
//
//            if (hasMessages(root)) {
//
//                processMessages(root);
//
//                return ApiResponse.success(
//                        "Messages processed successfully");
//            }
//
//
//            // -------------------------------------------------
//            // Message Status
//            // -------------------------------------------------
//
//            if (hasStatusUpdates(root)) {
//
//                processStatusUpdates(root);
//
//                return ApiResponse.success(
//                        "Status updates processed successfully");
//            }
//
//
//            return ApiResponse.success(
//                    "Webhook received but no actionable data");
//
//        } catch (Exception e) {
//
//            System.err.println(
//                    "Error processing webhook: "
//                            + e.getMessage());
//
//            e.printStackTrace();
//
//            return ApiResponse.error(
//                    "Error processing webhook: "
//                            + e.getMessage());
//        }
//    }
//
//
//    // =====================================================
//    // CHECK FOR MESSAGES
//    // =====================================================
//
//    private boolean hasMessages(
//            JsonNode root) {
//
//        if (!root.has("entry")) {
//
//            return false;
//        }
//
//        JsonNode entry =
//                root.path("entry");
//
//        for (JsonNode entryNode : entry) {
//
//            if (!entryNode.has("changes")) {
//
//                continue;
//            }
//
//            JsonNode changes =
//                    entryNode.path("changes");
//
//            for (JsonNode change : changes) {
//
//                if ("messages".equals(
//                        change.path("field").asText())) {
//
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
//
//
//    // =====================================================
//    // CHECK FOR STATUS UPDATES
//    // =====================================================
//
//    private boolean hasStatusUpdates(
//            JsonNode root) {
//
//        if (!root.has("entry")) {
//
//            return false;
//        }
//
//        JsonNode entry =
//                root.path("entry");
//
//        for (JsonNode entryNode : entry) {
//
//            if (!entryNode.has("changes")) {
//
//                continue;
//            }
//
//            JsonNode changes =
//                    entryNode.path("changes");
//
//            for (JsonNode change : changes) {
//
//                if ("statuses".equals(
//                        change.path("field").asText())) {
//
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
//
//
//    // =====================================================
//    // PROCESS MESSAGES
//    // =====================================================
//
//    private void processMessages(
//            JsonNode root) throws Exception {
//
//        JsonNode entry =
//                root.path("entry");
//
//        for (JsonNode entryNode : entry) {
//
//            JsonNode changes =
//                    entryNode.path("changes");
//
//            for (JsonNode change : changes) {
//
//                JsonNode value =
//                        change.path("value");
//
//                if (!value.has("messages")) {
//
//                    continue;
//                }
//
//                JsonNode messages =
//                        value.path("messages");
//
//                for (JsonNode message : messages) {
//
//                    processIncomingMessage(message);
//                }
//            }
//        }
//    }
//
//
//    // =====================================================
//    // PROCESS STATUS UPDATES
//    // =====================================================
//
//    private void processStatusUpdates(
//            JsonNode root) throws Exception {
//
//        JsonNode entry =
//                root.path("entry");
//
//        for (JsonNode entryNode : entry) {
//
//            JsonNode changes =
//                    entryNode.path("changes");
//
//            for (JsonNode change : changes) {
//
//                JsonNode value =
//                        change.path("value");
//
//                if (!value.has("statuses")) {
//
//                    continue;
//                }
//
//                JsonNode statuses =
//                        value.path("statuses");
//
//                for (JsonNode status : statuses) {
//
//                    processMessageStatus(status);
//                }
//            }
//        }
//    }
//
//
//    // =====================================================
//    // PROCESS INCOMING MESSAGE
//    // =====================================================
//
//    private void processIncomingMessage(
//            JsonNode message) {
//
//        try {
//
//            String from =
//                    message.path("from").asText();
//
//            String text =
//                    message
//                            .path("text")
//                            .path("body")
//                            .asText();
//
//            String messageId =
//                    message.path("id").asText();
//
//
//            System.out.println(
//                    "📩 WhatsApp message received");
//
//            System.out.println(
//                    "👤 From: " + from);
//
//            System.out.println(
//                    "📝 Text: " + text);
//
//            System.out.println(
//                    "🆔 Message ID: " + messageId);
//
//
//            // =================================================
//            // CHECK ADMIN COMMAND
//            // =================================================
//
//            if (isAdminNumber(from)) {
//
//                processAdminCommand(
//                        from,
//                        text);
//
//                return;
//            }
//
//
//            // =================================================
//            // CUSTOMER MESSAGE
//            // =================================================
//
//            String reply =
//                    generateAutoReply(text);
//
//            if (reply != null
//                    && !reply.isEmpty()) {
//
//                sendMessage(
//                        from,
//                        reply);
//
//                System.out.println(
//                        "📤 Auto-reply sent");
//            }
//
//        } catch (Exception e) {
//
//            System.err.println(
//                    "Error processing incoming message: "
//                            + e.getMessage());
//
//            e.printStackTrace();
//        }
//    }
//
//
//    // =====================================================
//    // CHECK ADMIN NUMBER
//    // =====================================================
//
//    private boolean isAdminNumber(
//            String from) {
//
//        if (from == null
//                || adminWhatsAppNumber == null
//                || adminWhatsAppNumber.isEmpty()) {
//
//            return false;
//        }
//
//        String formattedFrom =
//                formatPhoneNumber(from);
//
//        String formattedAdmin =
//                formatPhoneNumber(adminWhatsAppNumber);
//
//        return formattedFrom.equals(
//                formattedAdmin);
//    }
//
//
//    // =====================================================
//    // PROCESS ADMIN COMMAND
//    // =====================================================
//
//    private void processAdminCommand(
//            String from,
//            String text) {
//
//        try {
//
//            if (text == null
//                    || text.trim().isEmpty()) {
//
//                return;
//            }
//
//            String command =
//                    text.trim().toUpperCase();
//
//
//            // =================================================
//            // CONFIRM BOOKING
//            // =================================================
//
//            if (command.startsWith("CONFIRM")) {
//
//                Integer bookingId =
//                        extractBookingId(command);
//
//                if (bookingId == null) {
//
//                    sendMessage(
//                            from,
//                            "Please use format:\n\n"
//                                    + "CONFIRM 123");
//
//                    return;
//                }
//
//                updateBookingFromAdmin(
//                        bookingId,
//                        "CONFIRMED");
//
//                return;
//            }
//
//
//            // =================================================
//            // REJECT BOOKING
//            // =================================================
//
//            if (command.startsWith("REJECT")) {
//
//                Integer bookingId =
//                        extractBookingId(command);
//
//                if (bookingId == null) {
//
//                    sendMessage(
//                            from,
//                            "Please use format:\n\n"
//                                    + "REJECT 123");
//
//                    return;
//                }
//
//                updateBookingFromAdmin(
//                        bookingId,
//                        "REJECTED");
//
//                return;
//            }
//
//
//            // =================================================
//            // ADMIN HELP
//            // =================================================
//
//            if (command.equals("HELP")) {
//
//                sendMessage(
//                        from,
//                        """
//                        *ADMIN COMMANDS*
//
//                        CONFIRM 123
//                        REJECT 123
//                        """);
//
//                return;
//            }
//
//            // Unknown command
//            sendMessage(
//                    from,
//                    "Unknown command.\n\n"
//                            + "Use:\n"
//                            + "CONFIRM 123\n"
//                            + "REJECT 123");
//
//        } catch (Exception e) {
//
//            System.err.println(
//                    "Error processing admin command: "
//                            + e.getMessage());
//
//            sendMessage(
//                    from,
//                    "Error processing command.");
//        }
//    }
//
//
//    // =====================================================
//    // EXTRACT BOOKING ID
//    // =====================================================
//
//    private Integer extractBookingId(
//            String command) {
//
//        try {
//
//            String bookingId =
//                    command
//                            .replaceAll(
//                                    "[^0-9]",
//                                    "");
//
//            if (bookingId.isEmpty()) {
//
//                return null;
//            }
//
//            return Integer.parseInt(
//                    bookingId);
//
//        } catch (Exception e) {
//
//            return null;
//        }
//    }
//
//
//    // =====================================================
//    // UPDATE BOOKING FROM ADMIN
//    // =====================================================
//
//    @Transactional
//    private void updateBookingFromAdmin(
//            Integer bookingId,
//            String status) {
//
//        try {
//
//            Optional<Booking> bookingOpt =
//                    bookingRepository.findById(
//                            bookingId);
//
//            if (bookingOpt.isEmpty()) {
//
//                sendMessage(
//                        adminWhatsAppNumber,
//                        "❌ Booking #"
//                                + bookingId
//                                + " not found.");
//
//                return;
//            }
//
//
//            Booking booking =
//                    bookingOpt.get();
//
//
//            // -------------------------------------------------
//            // Update Booking Status
//            // -------------------------------------------------
//
//            booking.setBookingStatus(
//                    status);
//
//            booking.setWhatsappStatus(
//                    "ADMIN_" + status);
//
//            bookingRepository.save(
//                    booking);
//
//
//            System.out.println(
//                    "Booking #"
//                            + bookingId
//                            + " updated to "
//                            + status);
//
//
//            // -------------------------------------------------
//            // Notify Customer
//            // -------------------------------------------------
//
//            boolean sent =
//                    sendStatusUpdate(
//                            bookingId,
//                            status);
//
//
//            // -------------------------------------------------
//            // Notify Admin
//            // -------------------------------------------------
//
//            if (sent) {
//
//                sendMessage(
//                        adminWhatsAppNumber,
//                        "✅ Booking #"
//                                + bookingId
//                                + " marked as "
//                                + status
//                                + ".\n\n"
//                                + "Customer has been notified.");
//
//            } else {
//
//                sendMessage(
//                        adminWhatsAppNumber,
//                        "⚠️ Booking #"
//                                + bookingId
//                                + " updated to "
//                                + status
//                                + ", but customer WhatsApp notification failed.");
//            }
//
//        } catch (Exception e) {
//
//            System.err.println(
//                    "Error updating booking: "
//                            + e.getMessage());
//
//            e.printStackTrace();
//
//            sendMessage(
//                    adminWhatsAppNumber,
//                    "❌ Error updating booking #"
//                            + bookingId);
//        }
//    }
//
//
//    // =====================================================
//    // PROCESS MESSAGE STATUS
//    // =====================================================
//
//    private void processMessageStatus(
//            JsonNode status) {
//
//        try {
//
//            String statusType =
//                    status
//                            .path("status")
//                            .asText();
//
//            String recipientId =
//                    status
//                            .path("recipient_id")
//                            .asText();
//
//            String messageId =
//                    status
//                            .path("id")
//                            .asText();
//
//            System.out.println(
//                    "📊 WhatsApp Message Status: "
//                            + statusType);
//
//            System.out.println(
//                    "👤 Recipient: "
//                            + recipientId);
//
//            System.out.println(
//                    "🆔 Message ID: "
//                            + messageId);
//
//        } catch (Exception e) {
//
//            System.err.println(
//                    "Error processing message status: "
//                            + e.getMessage());
//        }
//    }
//
//
//    // =====================================================
//    // GENERATE AUTO REPLY
//    // =====================================================
//
//    private String generateAutoReply(
//            String message) {
//
//        if (message == null) {
//
//            return null;
//        }
//
//        String lowerMsg =
//                message
//                        .toLowerCase()
//                        .trim();
//
//
//        // -------------------------------------------------
//        // BOOKING
//        // -------------------------------------------------
//
//        if (lowerMsg.contains("book")
//                || lowerMsg.contains("booking")
//                || lowerMsg.contains("reserve")) {
//
//            return """
//                    Thank you for your interest in booking! 🚗
//
//                    Please visit our website to complete your booking.
//
//                    For assistance, please contact our support team.
//                    """;
//        }
//
//
//        // -------------------------------------------------
//        // STATUS
//        // -------------------------------------------------
//
//        if (lowerMsg.contains("status")
//                || lowerMsg.contains("update")) {
//
//            return """
//                    To check your booking status,
//                    please provide your Booking ID.
//
//                    Example:
//
//                    Status 123
//                    """;
//        }
//
//
//        // -------------------------------------------------
//        // CANCEL
//        // -------------------------------------------------
//
//        if (lowerMsg.contains("cancel")
//                || lowerMsg.contains("cancellation")) {
//
//            return """
//                    To cancel your booking,
//                    please provide your Booking ID.
//
//                    Example:
//
//                    Cancel 123
//
//                    Cancellation charges may apply according
//                    to our cancellation policy.
//                    """;
//        }
//
//
//        // -------------------------------------------------
//        // HELP
//        // -------------------------------------------------
//
//        if (lowerMsg.contains("help")
//                || lowerMsg.contains("support")) {
//
//            return """
//                    *CAR RENTAL SUPPORT* 🚗
//
//                    • Book a car
//                    • Check booking status
//                    • Cancel booking
//                    • Contact support
//
//                    Please type your requirement.
//                    """;
//        }
//
//
//        // -------------------------------------------------
//        // HELLO
//        // -------------------------------------------------
//
//        if (lowerMsg.equals("hi")
//                || lowerMsg.equals("hello")
//                || lowerMsg.equals("hey")) {
//
//            return """
//                    Hello! 👋
//
//                    Welcome to Car Rental Service 🚗
//
//                    How can I assist you?
//
//                    • Book a car 🚗
//                    • Check booking status 📋
//                    • Cancel booking ❌
//                    • Get help 🤝
//                    """;
//        }
//
//
//        return null;
//    }
//
//
//    // =====================================================
//    // HANDLE BOOKING REQUEST
//    // =====================================================
//
//    private void handleBookingRequest(
//            String from,
//            String text) {
//
//        try {
//
//            String response =
//                    "Thank you for your booking request! "
//                            + "Please visit our website to complete your booking.";
//
//            sendMessage(
//                    from,
//                    response);
//
//        } catch (Exception e) {
//
//            System.err.println(
//                    "Error handling booking request: "
//                            + e.getMessage());
//        }
//    }
//
//
//    // =====================================================
//    // HANDLE STATUS INQUIRY
//    // =====================================================
//
//    private void handleStatusInquiry(
//            String from,
//            String text) {
//
//        try {
//
//            String bookingIdStr =
//                    text.replaceAll(
//                            "[^0-9]",
//                            "");
//
//            if (!bookingIdStr.isEmpty()) {
//
//                Integer bookingId =
//                        Integer.parseInt(
//                                bookingIdStr);
//
//                Optional<Booking> bookingOpt =
//                        bookingRepository.findById(
//                                bookingId);
//
//                if (bookingOpt.isPresent()) {
//
//                    Booking booking =
//                            bookingOpt.get();
//
//                    String status =
//                            booking.getBookingStatus();
//
//                    String response =
//                            "Booking #"
//                                    + bookingId
//                                    + " is currently: "
//                                    + status;
//
//                    sendMessage(
//                            from,
//                            response);
//
//                } else {
//
//                    sendMessage(
//                            from,
//                            "Booking #"
//                                    + bookingId
//                                    + " not found.");
//                }
//
//            } else {
//
//                sendMessage(
//                        from,
//                        "Please provide your booking ID.");
//            }
//
//        } catch (Exception e) {
//
//            System.err.println(
//                    "Error handling status inquiry: "
//                            + e.getMessage());
//        }
//    }
//
//
//    // =====================================================
//    // SEND BOOKING CONFIRMATION
//    // =====================================================
//
//    public boolean sendBookingConfirmation(
//            Integer bookingId) {
//
//        try {
//
//            Optional<Booking> bookingOpt =
//                    bookingRepository.findById(
//                            bookingId);
//
//            if (bookingOpt.isEmpty()) {
//
//                System.err.println(
//                        "Booking not found: "
//                                + bookingId);
//
//                return false;
//            }
//
//
//            Booking booking =
//                    bookingOpt.get();
//
//            UserRequest user =
//                    booking.getUser();
//
//
//            String phoneNumber =
//                    user.getPhone();
//
//
//            if (phoneNumber == null
//                    || phoneNumber.isEmpty()) {
//
//                System.err.println(
//                        "No phone number for user: "
//                                + user.getId());
//
//                return false;
//            }
//
//
//            String message =
//                    buildBookingConfirmationMessage(
//                            booking);
//
//
//            return sendMessage(
//                    phoneNumber,
//                    message);
//
//        } catch (Exception e) {
//
//            System.err.println(
//                    "Error sending booking confirmation: "
//                            + e.getMessage());
//
//            return false;
//        }
//    }
//
//
//    // =====================================================
//    // SEND STATUS UPDATE
//    // =====================================================
//
//    public boolean sendStatusUpdate(
//            Integer bookingId,
//            String status) {
//
//        try {
//
//            Optional<Booking> bookingOpt =
//                    bookingRepository.findById(
//                            bookingId);
//
//            if (bookingOpt.isEmpty()) {
//
//                System.err.println(
//                        "Booking not found: "
//                                + bookingId);
//
//                return false;
//            }
//
//
//            Booking booking =
//                    bookingOpt.get();
//
//
//            UserRequest user =
//                    booking.getUser();
//
//
//            String phoneNumber =
//                    user.getPhone();
//
//
//            if (phoneNumber == null
//                    || phoneNumber.isEmpty()) {
//
//                System.err.println(
//                        "No phone number for user: "
//                                + user.getId());
//
//                return false;
//            }
//
//
//            String message =
//                    buildStatusUpdateMessage(
//                            booking,
//                            status);
//
//
//            boolean sent =
//                    sendMessage(
//                            phoneNumber,
//                            message);
//
//
//            if (sent
//                    && notificationService != null) {
//
//                notificationService.createNotification(
//
//                        user.getId(),
//
//                        "Booking Status Updated",
//
//                        "Your booking #"
//                                + bookingId
//                                + " is now "
//                                + status,
//
//                        "BOOKING",
//
//                        bookingId
//                );
//            }
//
//
//            return sent;
//
//        } catch (Exception e) {
//
//            System.err.println(
//                    "Error sending status update: "
//                            + e.getMessage());
//
//            return false;
//        }
//    }
//
//
//    // =====================================================
//    // SEND WHATSAPP MESSAGE
//    // =====================================================
//
//    public boolean sendMessage(
//            String phoneNumber,
//            String message) {
//
//        try {
//
//            // -------------------------------------------------
//            // Validate Phone
//            // -------------------------------------------------
//
//            String formattedPhone =
//                    formatPhoneNumber(
//                            phoneNumber);
//
//
//            if (formattedPhone.isEmpty()) {
//
//                System.err.println(
//                        "Invalid WhatsApp phone number");
//
//                return false;
//            }
//
//
//            System.out.println(
//                    "📤 Sending WhatsApp message to: "
//                            + formattedPhone);
//
//            System.out.println(
//                    "📝 Message: "
//                            + message);
//
//
//            // -------------------------------------------------
//            // Check API Configuration
//            // -------------------------------------------------
//
//            if (!isWebhookConfigured()) {
//
//                System.err.println(
//                        "WhatsApp API not configured.");
//
//                return false;
//            }
//
//
//            // -------------------------------------------------
//            // Request Body
//            // -------------------------------------------------
//
//            Map<String, Object> requestBody =
//                    new HashMap<>();
//
//            requestBody.put(
//                    "messaging_product",
//                    "whatsapp");
//
//            requestBody.put(
//                    "to",
//                    formattedPhone);
//
//            requestBody.put(
//                    "type",
//                    "text");
//
//
//            Map<String, String> textObj =
//                    new HashMap<>();
//
//            textObj.put(
//                    "body",
//                    message);
//
//
//            requestBody.put(
//                    "text",
//                    textObj);
//
//
//            // -------------------------------------------------
//            // Headers
//            // -------------------------------------------------
//
//            HttpHeaders headers =
//                    new HttpHeaders();
//
//            headers.setContentType(
//                    MediaType.APPLICATION_JSON);
//
//            headers.setBearerAuth(
//                    accessToken);
//
//
//            // -------------------------------------------------
//            // URL
//            // -------------------------------------------------
//
//            String url =
//                    whatsAppApiUrl
//                            + "/"
//                            + phoneNumberId
//                            + "/messages";
//
//
//            System.out.println(
//                    "🌐 WhatsApp API URL: "
//                            + url);
//
//
//            // -------------------------------------------------
//            // HTTP Request
//            // -------------------------------------------------
//
//            HttpEntity<Map<String, Object>> entity =
//                    new HttpEntity<>(
//                            requestBody,
//                            headers);
//
//
//            ResponseEntity<String> response =
//                    restTemplate.exchange(
//
//                            url,
//
//                            HttpMethod.POST,
//
//                            entity,
//
//                            String.class
//                    );
//
//
//            // -------------------------------------------------
//            // Response
//            // -------------------------------------------------
//
//            System.out.println(
//                    "WhatsApp API response status: "
//                            + response.getStatusCode());
//
//            System.out.println(
//                    "WhatsApp API response body: "
//                            + response.getBody());
//
//
//            if (response.getStatusCode()
//                    == HttpStatus.OK) {
//
//                System.out.println(
//                        "✅ WhatsApp message sent successfully");
//
//                return true;
//            }
//
//
//            System.err.println(
//                    "❌ WhatsApp message failed");
//
//            return false;
//
//        } catch (Exception e) {
//
//            System.err.println(
//                    "❌ Error sending WhatsApp message: "
//                            + e.getMessage());
//
//            e.printStackTrace();
//
//            return false;
//        }
//    }
//
//
//    // =====================================================
//    // BUILD BOOKING CONFIRMATION MESSAGE
//    // =====================================================
//
//    private String buildBookingConfirmationMessage(
//            Booking booking) {
//
//        DateTimeFormatter formatter =
//                DateTimeFormatter.ofPattern(
//                        "dd-MM-yyyy");
//
//
//        return String.format("""
//                🚗 *BOOKING CONFIRMED!* ✅
//
//                Booking ID: #%d
//
//                Car: %s %s
//
//                Pickup: %s
//                Dropoff: %s
//
//                Pickup Location: %s
//                Dropoff Location: %s
//
//                Total: ₹%.2f
//
//                Thank you for choosing our service! 💙
//                """,
//
//                booking.getId(),
//
//                booking.getAdminCarsData()
//                        .getBrand(),
//
//                booking.getAdminCarsData()
//                        .getName(),
//
//                booking.getPickupDate()
//                        .format(formatter),
//
//                booking.getDropoffDate()
//                        .format(formatter),
//
//                booking.getPickupLocation(),
//
//                booking.getDropoffLocation(),
//
//                booking.getTotal()
//        );
//    }
//
//
//    // =====================================================
//    // BUILD STATUS UPDATE MESSAGE
//    // =====================================================
//
//    private String buildStatusUpdateMessage(
//            Booking booking,
//            String status) {
//
//        String statusEmoji =
//                getStatusEmoji(status);
//
//        String statusMessage =
//                getStatusMessage(status);
//
//
//        return String.format("""
//                %s *BOOKING STATUS UPDATE*
//
//                Booking ID: #%d
//
//                Status: %s
//
//                Car: %s %s
//
//                %s
//
//                Thank you for choosing our service. 💙
//                """,
//
//                statusEmoji,
//
//                booking.getId(),
//
//                status,
//
//                booking.getAdminCarsData()
//                        .getBrand(),
//
//                booking.getAdminCarsData()
//                        .getName(),
//
//                statusMessage
//        );
//    }
//
//
//    // =====================================================
//    // STATUS EMOJI
//    // =====================================================
//
//    private String getStatusEmoji(
//            String status) {
//
//        if (status == null) {
//
//            return "📋";
//        }
//
//
//        switch (status.toUpperCase()) {
//
//            case "CONFIRMED":
//                return "✅";
//
//            case "REJECTED":
//                return "❌";
//
//            case "PENDING":
//                return "⏳";
//
//            case "CANCELLED":
//                return "📅";
//
//            default:
//                return "📋";
//        }
//    }
//
//
//    // =====================================================
//    // STATUS MESSAGE
//    // =====================================================
//
//    private String getStatusMessage(
//            String status) {
//
//        if (status == null) {
//
//            return "Your booking status has been updated.";
//        }
//
//
//        switch (status.toUpperCase()) {
//
//            case "CONFIRMED":
//
//                return "🎉 Your booking has been confirmed! "
//                        + "We look forward to serving you.";
//
//
//            case "REJECTED":
//
//                return "😔 We regret to inform you "
//                        + "that your booking has been rejected.";
//
//
//            case "PENDING":
//
//                return "⏳ Your booking is still pending review. "
//                        + "We'll update you soon.";
//
//
//            case "CANCELLED":
//
//                return "📅 Your booking has been cancelled "
//                        + "as requested.";
//
//
//            default:
//
//                return "Your booking status has been updated to: "
//                        + status;
//        }
//    }
//
//
//    // =====================================================
//    // FORMAT PHONE NUMBER
//    // =====================================================
//
//    private String formatPhoneNumber(
//            String phoneNumber) {
//
//        if (phoneNumber == null) {
//
//            return "";
//        }
//
//
//        String cleaned =
//                phoneNumber.replaceAll(
//                        "[^0-9]",
//                        "");
//
//
//        /*
//         * Indian 10-digit number
//         *
//         * 9876543210
//         *
//         * becomes
//         *
//         * 919876543210
//         */
//
//        if (!cleaned.startsWith("91")
//                && cleaned.length() == 10) {
//
//            cleaned =
//                    "91" + cleaned;
//        }
//
//
//        return cleaned;
//    }
//}
package com.My_Car_Rental_Application.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.My_Car_Rental_Application.dto.ApiResponse;
import com.My_Car_Rental_Application.dto.WhatsAppBookingRequest;
import com.My_Car_Rental_Application.entity.AdminCarsData;
import com.My_Car_Rental_Application.entity.Booking;
import com.My_Car_Rental_Application.entity.UserRequest;
import com.My_Car_Rental_Application.repository.BookingRepository;
import com.My_Car_Rental_Application.repository.CarsDataRepository;
import com.My_Car_Rental_Application.repository.HomeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class WhatsAppWebhookService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HomeRepository homeRepository;

    @Autowired
    private CarsDataRepository carsDataRepository;

    @Autowired(required = false)
    private NotificationService notificationService;

    // =========================================================
    // WHATSAPP CONFIGURATION
    // =========================================================

    /*
     * Example:
     *
     * whatsapp.api.url=https://graph.facebook.com/v23.0
     *
     * Keep this configurable because Meta Graph API versions
     * change over time.
     */
    @Value("${whatsapp.api.url:https://graph.facebook.com/v23.0}")
    private String whatsAppApiUrl;

    /*
     * WhatsApp Business Phone Number ID from Meta.
     */
    @Value("${whatsapp.phone.number.id:}")
    private String phoneNumberId;

    /*
     * Meta WhatsApp Cloud API access token.
     */
    @Value("${whatsapp.access.token:}")
    private String accessToken;

    /*
     * Token used during Meta webhook verification.
     */
    @Value("${whatsapp.verify.token:your_verify_token_here}")
    private String verifyToken;

    /*
     * Admin WhatsApp number.
     *
     * Example:
     *
     * 919876543210
     */
    @Value("${whatsapp.admin.number:}")
    private String adminWhatsAppNumber;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();


    // =========================================================
    // CHECK WHATSAPP CONFIGURATION
    // =========================================================

    private boolean isWebhookConfigured() {

        return isNotBlank(phoneNumberId)
                && isNotBlank(accessToken)
                && isNotBlank(adminWhatsAppNumber)
                && !phoneNumberId.equalsIgnoreCase(
                        "your_phone_number_id_here")
                && !accessToken.equalsIgnoreCase(
                        "your_access_token_here");
    }


    // =========================================================
    // CREATE WHATSAPP BOOKING
    // =========================================================

//    @Transactional
//    public Map<String, Object> createWhatsAppBooking(
//            WhatsAppBookingRequest request) {
//
//        if (request == null) {
//            throw new RuntimeException(
//                    "Booking request cannot be null");
//        }
//
//        // -----------------------------------------------------
//        // Find User
//        // -----------------------------------------------------
//
//        UserRequest user = homeRepository
//                .findById(request.getUserId())
//                .orElseThrow(() ->
//                        new RuntimeException(
//                                "User not found: "
//                                        + request.getUserId()));
//
//
//        // -----------------------------------------------------
//        // Find Car
//        // -----------------------------------------------------
//
//        AdminCarsData car = carsDataRepository
//                .findById(request.getCarId())
//                .orElseThrow(() ->
//                        new RuntimeException(
//                                "Car not found: "
//                                        + request.getCarId()));
//
//
//        // -----------------------------------------------------
//        // Validate Dates
//        // -----------------------------------------------------
//
//        if (request.getPickupDate() == null
//                || request.getDropoffDate() == null) {
//
//            throw new RuntimeException(
//                    "Pickup date and dropoff date are required");
//        }
//
//        if (request.getDropoffDate()
//                .isBefore(request.getPickupDate())) {
//
//            throw new RuntimeException(
//                    "Dropoff date cannot be before pickup date");
//        }
//
//
//        // -----------------------------------------------------
//        // Validate Admin Number
//        // -----------------------------------------------------
//
//        if (!isNotBlank(adminWhatsAppNumber)) {
//
//            throw new RuntimeException(
//                    "Admin WhatsApp number is not configured. "
//                            + "Add whatsapp.admin.number "
//                            + "in application.properties");
//        }
//
//
//        // -----------------------------------------------------
//        // Create Booking
//        // -----------------------------------------------------
//
//        Booking booking = new Booking();
//
//        booking.setUser(user);
//
//        booking.setAdminCarsData(car);
//
//        booking.setPickupDate(
//                request.getPickupDate());
//
//        booking.setDropoffDate(
//                request.getDropoffDate());
//
//        booking.setPickupLocation(
//                request.getPickupLocation());
//
//        booking.setDropoffLocation(
//                request.getDropoffLocation());
//
//        booking.setTripDriverType(
//                request.getTripDriverType());
//
//        booking.setDays(
//                request.getDays());
//
//        booking.setPrice(
//                request.getPrice());
//
//        booking.setTotal(
//                request.getTotal());
//
//        booking.setBookingStatus(
//                "WHATSAPP_PENDING");
//
//        booking.setBookingSource(
//                "WHATSAPP");
//
//        booking.setWhatsappStatus(
//                "PENDING");
//
//
//        // -----------------------------------------------------
//        // Save Booking
//        // -----------------------------------------------------
//
//        Booking savedBooking =
//                bookingRepository.save(booking);
//
//
//        // -----------------------------------------------------
//        // Create Admin Message
//        // -----------------------------------------------------
//
//        String message =
//                createAdminBookingMessage(
//                        savedBooking);
//
//
//        // -----------------------------------------------------
//        // Send WhatsApp Message
//        // -----------------------------------------------------
//
//        boolean sent =
//                sendMessage(
//                        adminWhatsAppNumber,
//                        message);
//
//
//        // -----------------------------------------------------
//        // Update WhatsApp Status
//        // -----------------------------------------------------
//
//        if (sent) {
//
//            savedBooking.setWhatsappStatus(
//                    "SENT");
//
//            bookingRepository.save(
//                    savedBooking);
//
//        } else {
//
//            savedBooking.setWhatsappStatus(
//                    "FAILED");
//
//            bookingRepository.save(
//                    savedBooking);
//
//            throw new RuntimeException(
//                    "Booking created successfully, "
//                            + "but WhatsApp message could not be sent");
//        }
//
//
//        // -----------------------------------------------------
//        // Response
//        // -----------------------------------------------------
//
//        Map<String, Object> response =
//                new HashMap<>();
//
//        response.put(
//                "success",
//                true);
//
//        response.put(
//                "bookingId",
//                savedBooking.getId());
//
//        response.put(
//                "status",
//                savedBooking.getBookingStatus());
//
//        response.put(
//                "whatsappStatus",
//                savedBooking.getWhatsappStatus());
//
//        response.put(
//                "message",
//                "Booking request sent to admin on WhatsApp");
//
//        return response;
//    }
//
    @Transactional
    public Map<String, Object> createWhatsAppBooking(WhatsAppBookingRequest request) {

        if (request == null) {
            throw new RuntimeException("Booking request cannot be null");
        }

        // 1. Validate User
        UserRequest user = homeRepository
                .findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        // 2. Validate Car
        AdminCarsData car = carsDataRepository
                .findById(request.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found: " + request.getCarId()));

        // 3. Validate Dates
        if (request.getPickupDate() == null || request.getDropoffDate() == null) {
            throw new RuntimeException("Pickup date and dropoff date are required");
        }

        if (request.getDropoffDate().isBefore(request.getPickupDate())) {
            throw new RuntimeException("Dropoff date cannot be before pickup date");
        }

        // 4. Create Booking Entry
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setAdminCarsData(car);
        booking.setPickupDate(request.getPickupDate());
        booking.setDropoffDate(request.getDropoffDate());
        booking.setPickupLocation(request.getPickupLocation());
        booking.setDropoffLocation(request.getDropoffLocation());
        booking.setTripDriverType(request.getTripDriverType());
        booking.setDays(request.getDays());
        booking.setPrice(request.getPrice());
        booking.setTotal(request.getTotal());

        // Set default initial statuses
        booking.setBookingStatus("PENDING");
        booking.setBookingSource("WHATSAPP");
        booking.setWhatsappStatus("PENDING");

        Booking savedBooking = bookingRepository.save(booking);

        // 5. Return HTTP 200 OK Response Payload
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("bookingId", savedBooking.getId());
        response.put("status", savedBooking.getBookingStatus());
        response.put("whatsappStatus", savedBooking.getWhatsappStatus());
        response.put("booking", savedBooking);
        response.put("message", "Booking saved successfully");

        return response;
    }
    // =========================================================
    // CREATE ADMIN BOOKING MESSAGE
    // =========================================================
    
 // =====================================================
 // ADMIN STATUS UPDATE & USER WHATSAPP NOTIFICATION
 // =====================================================

 @PostMapping("/admin/update-status")
 public ResponseEntity<?> updateBookingStatus(
         @RequestParam Long bookingId,
         @RequestParam String newStatus,
         @RequestParam String customerPhone) {

     try {
         // 1. Build status notification message for user
         String message = String.format(
             "Hello! Your booking status for Booking ID #%d has been updated to: *%s*.",
             bookingId, newStatus
         );

         // 2. Format customer phone (Ensure no leading + or spaces)
         String cleanPhone = customerPhone.replaceAll("[^0-9]", "");

         // 3. Create WhatsApp link for admin to click & send to user
         String whatsappUrl = "https://wa.me/" + cleanPhone + "?text=" + 
                              java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);

         return ResponseEntity.ok(Map.of(
             "success", true,
             "message", "Status updated successfully",
             "whatsappUrl", whatsappUrl
         ));

     } catch (Exception e) {
         return ResponseEntity
                 .status(HttpStatus.INTERNAL_SERVER_ERROR)
                 .body(Map.of("success", false, "message", e.getMessage()));
     }
 }

    private String createAdminBookingMessage(Booking booking) {

        UserRequest user =
                booking.getUser();

        AdminCarsData car =
                booking.getAdminCarsData();

        return """
                🚗 *NEW CAR BOOKING REQUEST*

                ━━━━━━━━━━━━━━━━━━━━
                🚘 *CAR DETAILS*
                ━━━━━━━━━━━━━━━━━━━━

                Booking ID: %d
                Car: %s
                Brand: %s
                Daily Rate: ₹%.0f

                ━━━━━━━━━━━━━━━━━━━━
                📅 *TRIP DETAILS*
                ━━━━━━━━━━━━━━━━━━━━

                Pickup Date: %s
                Dropoff Date: %s
                Pickup Location: %s
                Dropoff Location: %s
                Trip Type: %s

                ━━━━━━━━━━━━━━━━━━━━
                👤 *CUSTOMER DETAILS*
                ━━━━━━━━━━━━━━━━━━━━

                Name: %s %s
                Email: %s
                Phone: %s

                ━━━━━━━━━━━━━━━━━━━━
                💰 *BOOKING SUMMARY*
                ━━━━━━━━━━━━━━━━━━━━

                Rental Days: %d
                Daily Rate: ₹%.0f
                Total: ₹%.0f

                ━━━━━━━━━━━━━━━━━━━━

                *ADMIN ACTION*

                Reply:

                CONFIRM %d

                or

                REJECT %d

                Thank you.
                """.formatted(

                booking.getId(),

                safe(car.getName()),

                safe(car.getBrand()),

                booking.getPrice(),

                booking.getPickupDate(),

                booking.getDropoffDate(),

                safe(booking.getPickupLocation()),

                safe(booking.getDropoffLocation()),

                safe(booking.getTripDriverType()),

                safe(user.getFirstName()),

                safe(user.getLastName()),

                safe(user.getEmail()),

                safe(user.getPhone()),

                booking.getDays(),

                booking.getPrice(),

                booking.getTotal(),

                booking.getId(),

                booking.getId()
        );
    }


    // =========================================================
    // VALIDATE WEBHOOK PAYLOAD
    // =========================================================

    private boolean isValidWhatsAppPayload(
            Map<String, Object> payload) {

        if (payload == null
                || payload.isEmpty()) {

            return false;
        }

        return payload.containsKey("entry")
                || payload.containsKey("object");
    }


    // =========================================================
    // GET WEBHOOK STATUS
    // =========================================================

    public String getWebhookStatus() {

        try {

            Map<String, Object> status =
                    new HashMap<>();

            status.put(
                    "configured",
                    isWebhookConfigured());

            status.put(
                    "phoneNumberId",
                    isNotBlank(phoneNumberId)
                            ? "SET"
                            : "NOT SET");

            status.put(
                    "accessToken",
                    isNotBlank(accessToken)
                            ? "SET"
                            : "NOT SET");

            status.put(
                    "verifyToken",
                    isNotBlank(verifyToken)
                            ? "SET"
                            : "NOT SET");

            status.put(
                    "adminWhatsAppNumber",
                    isNotBlank(adminWhatsAppNumber)
                            ? "SET"
                            : "NOT SET");

            status.put(
                    "apiUrl",
                    whatsAppApiUrl);

            status.put(
                    "timestamp",
                    LocalDateTime.now().toString());

            return objectMapper.writeValueAsString(
                    status);

        } catch (Exception e) {

            return "{\"error\":\""
                    + escapeJson(e.getMessage())
                    + "\"}";
        }
    }


    // =========================================================
    // PROCESS WEBHOOK
    // =========================================================

    public ApiResponse processWebhook(
            Map<String, Object> payload) {

        try {

            System.out.println(
                    "================================================");

            System.out.println(
                    "Processing WhatsApp webhook payload");

            System.out.println(
                    "================================================");


            // -------------------------------------------------
            // Validate Payload
            // -------------------------------------------------

            if (!isValidWhatsAppPayload(payload)) {

                return ApiResponse.error(
                        "Invalid WhatsApp webhook payload");
            }


            // -------------------------------------------------
            // Convert Payload To JSON
            // -------------------------------------------------

            String payloadJson =
                    objectMapper.writeValueAsString(
                            payload);

            JsonNode root =
                    objectMapper.readTree(
                            payloadJson);


            System.out.println(
                    "Webhook JSON: "
                            + payloadJson);


            boolean processed =
                    false;


            // -------------------------------------------------
            // Process Messages
            // -------------------------------------------------

            if (hasMessages(root)) {

                processMessages(root);

                processed = true;
            }


            // -------------------------------------------------
            // Process Statuses
            // -------------------------------------------------

            if (hasStatusUpdates(root)) {

                processStatusUpdates(root);

                processed = true;
            }


            if (processed) {

                return ApiResponse.success(
                        "WhatsApp webhook processed successfully");
            }


            return ApiResponse.success(
                    "Webhook received but no actionable data");


        } catch (Exception e) {

            System.err.println(
                    "Error processing WhatsApp webhook: "
                            + e.getMessage());

            e.printStackTrace();

            return ApiResponse.error(
                    "Error processing webhook: "
                            + e.getMessage());
        }
    }


    // =========================================================
    // CHECK FOR MESSAGES
    // =========================================================

    private boolean hasMessages(
            JsonNode root) {

        JsonNode entry =
                root.path("entry");

        if (!entry.isArray()) {

            return false;
        }

        for (JsonNode entryNode : entry) {

            JsonNode changes =
                    entryNode.path("changes");

            if (!changes.isArray()) {

                continue;
            }

            for (JsonNode change : changes) {

                if ("messages".equals(
                        change.path("field").asText())) {

                    return true;
                }
            }
        }

        return false;
    }


    // =========================================================
    // CHECK FOR STATUS UPDATES
    // =========================================================

    private boolean hasStatusUpdates(
            JsonNode root) {

        JsonNode entry =
                root.path("entry");

        if (!entry.isArray()) {

            return false;
        }

        for (JsonNode entryNode : entry) {

            JsonNode changes =
                    entryNode.path("changes");

            if (!changes.isArray()) {

                continue;
            }

            for (JsonNode change : changes) {

                if ("statuses".equals(
                        change.path("field").asText())) {

                    return true;
                }
            }
        }

        return false;
    }


    // =========================================================
    // PROCESS MESSAGES
    // =========================================================

    private void processMessages(
            JsonNode root) {

        JsonNode entry =
                root.path("entry");

        if (!entry.isArray()) {

            return;
        }

        for (JsonNode entryNode : entry) {

            JsonNode changes =
                    entryNode.path("changes");

            if (!changes.isArray()) {

                continue;
            }

            for (JsonNode change : changes) {

                JsonNode value =
                        change.path("value");

                JsonNode messages =
                        value.path("messages");

                if (!messages.isArray()) {

                    continue;
                }

                for (JsonNode message : messages) {

                    processIncomingMessage(
                            message);
                }
            }
        }
    }


    // =========================================================
    // PROCESS STATUS UPDATES
    // =========================================================

    private void processStatusUpdates(
            JsonNode root) {

        JsonNode entry =
                root.path("entry");

        if (!entry.isArray()) {

            return;
        }

        for (JsonNode entryNode : entry) {

            JsonNode changes =
                    entryNode.path("changes");

            if (!changes.isArray()) {

                continue;
            }

            for (JsonNode change : changes) {

                JsonNode value =
                        change.path("value");

                JsonNode statuses =
                        value.path("statuses");

                if (!statuses.isArray()) {

                    continue;
                }

                for (JsonNode status : statuses) {

                    processMessageStatus(
                            status);
                }
            }
        }
    }


    // =========================================================
    // PROCESS INCOMING MESSAGE
    // =========================================================

    private void processIncomingMessage(
            JsonNode message) {

        try {

            String from =
                    message.path("from")
                            .asText("");

            String messageId =
                    message.path("id")
                            .asText("");

            String messageType =
                    message.path("type")
                            .asText("");


            System.out.println(
                    "📩 WhatsApp message received");

            System.out.println(
                    "👤 From: " + from);

            System.out.println(
                    "🆔 Message ID: " + messageId);

            System.out.println(
                    "📦 Type: " + messageType);


            if (from.isEmpty()) {

                System.err.println(
                        "Incoming message does not contain sender");

                return;
            }


            // -------------------------------------------------
            // Only process text messages
            // -------------------------------------------------

            if (!"text".equalsIgnoreCase(
                    messageType)) {

                sendMessage(
                        from,
                        "Currently, only text commands are supported.");

                return;
            }


            String text =
                    message.path("text")
                            .path("body")
                            .asText("");


            System.out.println(
                    "📝 Text: " + text);


            if (text.trim().isEmpty()) {

                return;
            }


            // -------------------------------------------------
            // ADMIN MESSAGE
            // -------------------------------------------------

            if (isAdminNumber(from)) {

                System.out.println(
                        "🔐 Message received from ADMIN");

                processAdminCommand(
                        from,
                        text);

                return;
            }


            // -------------------------------------------------
            // CUSTOMER MESSAGE
            // -------------------------------------------------

            String reply =
                    generateAutoReply(text);


            if (reply != null
                    && !reply.isBlank()) {

                boolean sent =
                        sendMessage(
                                from,
                                reply);

                System.out.println(
                        "📤 Customer auto-reply sent: "
                                + sent);
            }

        } catch (Exception e) {

            System.err.println(
                    "Error processing incoming message: "
                            + e.getMessage());

            e.printStackTrace();
        }
    }


    // =========================================================
    // CHECK ADMIN NUMBER
    // =========================================================

    private boolean isAdminNumber(
            String from) {

        if (!isNotBlank(from)
                || !isNotBlank(adminWhatsAppNumber)) {

            return false;
        }

        String formattedFrom =
                formatPhoneNumber(from);

        String formattedAdmin =
                formatPhoneNumber(
                        adminWhatsAppNumber);

        return formattedFrom.equals(
                formattedAdmin);
    }


    // =========================================================
    // PROCESS ADMIN COMMAND
    // =========================================================

    private void processAdminCommand(
            String from,
            String text) {

        try {

            if (!isNotBlank(text)) {

                return;
            }

            String command =
                    text.trim()
                            .toUpperCase();


            // -------------------------------------------------
            // CONFIRM
            // -------------------------------------------------

            if (command.matches(
                    "^CONFIRM\\s+\\d+$")) {

                Integer bookingId =
                        extractBookingId(
                                command);

                updateBookingFromAdmin(
                        bookingId,
                        "CONFIRMED");

                return;
            }


            // -------------------------------------------------
            // REJECT
            // -------------------------------------------------

            if (command.matches(
                    "^REJECT\\s+\\d+$")) {

                Integer bookingId =
                        extractBookingId(
                                command);

                updateBookingFromAdmin(
                        bookingId,
                        "REJECTED");

                return;
            }


            // -------------------------------------------------
            // HELP
            // -------------------------------------------------

            if (command.equals("HELP")) {

                sendMessage(
                        from,
                        """
                        *ADMIN COMMANDS*

                        CONFIRM 123

                        REJECT 123

                        HELP
                        """);

                return;
            }


            // -------------------------------------------------
            // UNKNOWN COMMAND
            // -------------------------------------------------

            sendMessage(
                    from,
                    """
                    ❌ Unknown command.

                    Use:

                    CONFIRM 123

                    REJECT 123

                    HELP
                    """);

        } catch (Exception e) {

            System.err.println(
                    "Error processing admin command: "
                            + e.getMessage());

            e.printStackTrace();

            sendMessage(
                    from,
                    "❌ Error processing admin command.");
        }
    }


    // =========================================================
    // EXTRACT BOOKING ID
    // =========================================================

    private Integer extractBookingId(
            String command) {

        try {

            String[] parts =
                    command.trim()
                            .split("\\s+");

            if (parts.length != 2) {

                return null;
            }

            return Integer.parseInt(
                    parts[1]);

        } catch (Exception e) {

            return null;
        }
    }


    // =========================================================
    // UPDATE BOOKING FROM ADMIN
    // =========================================================

    /*
     * IMPORTANT:
     *
     * Do not put @Transactional on this private method.
     *
     * Spring @Transactional works through proxies and therefore
     * private methods are not proxied.
     *
     * The public createWhatsAppBooking() method is transactional.
     *
     * If you specifically need a separate transaction here,
     * move this method to another @Service.
     */
    private void updateBookingFromAdmin(
            Integer bookingId,
            String status) {

        try {

            if (bookingId == null) {

                sendMessage(
                        adminWhatsAppNumber,
                        "❌ Invalid booking ID.");

                return;
            }


            Optional<Booking> bookingOpt =
                    bookingRepository.findById(
                            bookingId);


            if (bookingOpt.isEmpty()) {

                sendMessage(
                        adminWhatsAppNumber,
                        "❌ Booking #"
                                + bookingId
                                + " not found.");

                return;
            }


            Booking booking =
                    bookingOpt.get();


            // -------------------------------------------------
            // Prevent duplicate confirmation/rejection
            // -------------------------------------------------

            String currentStatus =
                    booking.getBookingStatus();


            if ("CONFIRMED".equalsIgnoreCase(
                    currentStatus)
                    || "REJECTED".equalsIgnoreCase(
                    currentStatus)) {

                sendMessage(
                        adminWhatsAppNumber,
                        "⚠️ Booking #"
                                + bookingId
                                + " is already "
                                + currentStatus
                                + ".");

                return;
            }


            // -------------------------------------------------
            // Update Booking
            // -------------------------------------------------

            booking.setBookingStatus(
                    status);

            booking.setWhatsappStatus(
                    "ADMIN_" + status);


            bookingRepository.save(
                    booking);


            System.out.println(
                    "✅ Booking #"
                            + bookingId
                            + " updated to "
                            + status);


            // -------------------------------------------------
            // Notify Customer
            // -------------------------------------------------

            boolean customerNotified =
                    sendStatusUpdate(
                            bookingId,
                            status);


            // -------------------------------------------------
            // Notify Admin
            // -------------------------------------------------

            if (customerNotified) {

                sendMessage(
                        adminWhatsAppNumber,
                        "✅ Booking #"
                                + bookingId
                                + " marked as "
                                + status
                                + ".\n\n"
                                + "Customer has been notified.");

            } else {

                sendMessage(
                        adminWhatsAppNumber,
                        "⚠️ Booking #"
                                + bookingId
                                + " updated to "
                                + status
                                + ", but customer WhatsApp "
                                + "notification failed.");
            }

        } catch (Exception e) {

            System.err.println(
                    "Error updating booking: "
                            + e.getMessage());

            e.printStackTrace();

            sendMessage(
                    adminWhatsAppNumber,
                    "❌ Error updating booking #"
                            + bookingId);
        }
    }


    // =========================================================
    // PROCESS MESSAGE STATUS
    // =========================================================

    private void processMessageStatus(
            JsonNode status) {

        try {

            String statusType =
                    status.path("status")
                            .asText("");

            String recipientId =
                    status.path("recipient_id")
                            .asText("");

            String messageId =
                    status.path("id")
                            .asText("");


            System.out.println(
                    "📊 WhatsApp Message Status: "
                            + statusType);

            System.out.println(
                    "👤 Recipient: "
                            + recipientId);

            System.out.println(
                    "🆔 Message ID: "
                            + messageId);

        } catch (Exception e) {

            System.err.println(
                    "Error processing message status: "
                            + e.getMessage());
        }
    }


    // =========================================================
    // GENERATE CUSTOMER AUTO REPLY
    // =========================================================

    private String generateAutoReply(
            String message) {

        if (!isNotBlank(message)) {

            return null;
        }

        String lowerMsg =
                message.toLowerCase()
                        .trim();


        // -----------------------------------------------------
        // BOOKING
        // -----------------------------------------------------

        if (lowerMsg.equals("book")
                || lowerMsg.equals("booking")
                || lowerMsg.equals("reserve")
                || lowerMsg.contains("want to book")) {

            return """
                    Thank you for your interest in booking! 🚗

                    Please visit our website to complete your booking.

                    Our team will assist you with your car rental.
                    """;
        }


        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        if (lowerMsg.matches(
                "status\\s+\\d+")) {

            handleStatusInquiry(
                    null,
                    message);

            return null;
        }


        if (lowerMsg.equals("status")
                || lowerMsg.contains("booking status")) {

            return """
                    To check your booking status,
                    please provide your Booking ID.

                    Example:

                    Status 123
                    """;
        }


        // -----------------------------------------------------
        // CANCEL
        // -----------------------------------------------------

        if (lowerMsg.equals("cancel")
                || lowerMsg.contains("cancellation")) {

            return """
                    To cancel your booking,
                    please provide your Booking ID.

                    Example:

                    Cancel 123

                    Cancellation charges may apply
                    according to our cancellation policy.
                    """;
        }


        // -----------------------------------------------------
        // HELP
        // -----------------------------------------------------

        if (lowerMsg.equals("help")
                || lowerMsg.contains("support")) {

            return """
                    *CAR RENTAL SUPPORT* 🚗

                    • Book a car
                    • Check booking status
                    • Cancel booking
                    • Contact support

                    Please type your requirement.
                    """;
        }


        // -----------------------------------------------------
        // HELLO
        // -----------------------------------------------------

        if (lowerMsg.equals("hi")
                || lowerMsg.equals("hello")
                || lowerMsg.equals("hey")) {

            return """
                    Hello! 👋

                    Welcome to Car Rental Service 🚗

                    How can I assist you?

                    • Book a car 🚗
                    • Check booking status 📋
                    • Cancel booking ❌
                    • Get help 🤝
                    """;
        }


        return null;
    }


    // =========================================================
    // HANDLE BOOKING REQUEST
    // =========================================================

    private void handleBookingRequest(
            String from,
            String text) {

        try {

            String response =
                    """
                    Thank you for your booking request! 🚗

                    Please visit our website to complete your booking.
                    """;

            if (isNotBlank(from)) {

                sendMessage(
                        from,
                        response);
            }

        } catch (Exception e) {

            System.err.println(
                    "Error handling booking request: "
                            + e.getMessage());
        }
    }


    // =========================================================
    // HANDLE STATUS INQUIRY
    // =========================================================

    private void handleStatusInquiry(
            String from,
            String text) {

        try {

            if (!isNotBlank(text)) {

                if (isNotBlank(from)) {

                    sendMessage(
                            from,
                            "Please provide your booking ID.");
                }

                return;
            }


            String bookingIdStr =
                    text.replaceAll(
                            "[^0-9]",
                            "");


            if (bookingIdStr.isEmpty()) {

                if (isNotBlank(from)) {

                    sendMessage(
                            from,
                            "Please provide a valid booking ID.");
                }

                return;
            }


            Integer bookingId =
                    Integer.parseInt(
                            bookingIdStr);


            Optional<Booking> bookingOpt =
                    bookingRepository.findById(
                            bookingId);


            if (bookingOpt.isEmpty()) {

                if (isNotBlank(from)) {

                    sendMessage(
                            from,
                            "Booking #"
                                    + bookingId
                                    + " not found.");
                }

                return;
            }


            Booking booking =
                    bookingOpt.get();


            String status =
                    booking.getBookingStatus();


            String response =
                    "📋 Booking #"
                            + bookingId
                            + " is currently: "
                            + status;


            if (isNotBlank(from)) {

                sendMessage(
                        from,
                        response);
            }

        } catch (Exception e) {

            System.err.println(
                    "Error handling status inquiry: "
                            + e.getMessage());
        }
    }


    // =========================================================
    // SEND BOOKING CONFIRMATION
    // =========================================================

    public boolean sendBookingConfirmation(
            Integer bookingId) {

        try {

            Optional<Booking> bookingOpt =
                    bookingRepository.findById(
                            bookingId);


            if (bookingOpt.isEmpty()) {

                System.err.println(
                        "Booking not found: "
                                + bookingId);

                return false;
            }


            Booking booking =
                    bookingOpt.get();


            UserRequest user =
                    booking.getUser();


            if (user == null) {

                System.err.println(
                        "User not found for booking: "
                                + bookingId);

                return false;
            }


            String phoneNumber =
                    user.getPhone();


            if (!isNotBlank(phoneNumber)) {

                System.err.println(
                        "No phone number for user: "
                                + user.getId());

                return false;
            }


            String message =
                    buildBookingConfirmationMessage(
                            booking);


            return sendMessage(
                    phoneNumber,
                    message);

        } catch (Exception e) {

            System.err.println(
                    "Error sending booking confirmation: "
                            + e.getMessage());

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // SEND STATUS UPDATE
    // =========================================================

    public boolean sendStatusUpdate(
            Integer bookingId,
            String status) {

        try {

            Optional<Booking> bookingOpt =
                    bookingRepository.findById(
                            bookingId);


            if (bookingOpt.isEmpty()) {

                System.err.println(
                        "Booking not found: "
                                + bookingId);

                return false;
            }


            Booking booking =
                    bookingOpt.get();


            UserRequest user =
                    booking.getUser();


            if (user == null) {

                System.err.println(
                        "User not found for booking: "
                                + bookingId);

                return false;
            }


            String phoneNumber =
                    user.getPhone();


            if (!isNotBlank(phoneNumber)) {

                System.err.println(
                        "No phone number for user: "
                                + user.getId());

                return false;
            }


            String message =
                    buildStatusUpdateMessage(
                            booking,
                            status);


            boolean sent =
                    sendMessage(
                            phoneNumber,
                            message);


            // -------------------------------------------------
            // Create application notification
            // -------------------------------------------------

            if (sent
                    && notificationService != null) {

                try {

                    notificationService.createNotification(

                            user.getId(),

                            "Booking Status Updated",

                            "Your booking #"
                                    + bookingId
                                    + " is now "
                                    + status,

                            "BOOKING",

                            bookingId
                    );

                } catch (Exception notificationError) {

                    System.err.println(
                            "Notification creation failed: "
                                    + notificationError.getMessage());
                }
            }


            return sent;

        } catch (Exception e) {

            System.err.println(
                    "Error sending status update: "
                            + e.getMessage());

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // SEND WHATSAPP MESSAGE
    // =========================================================

    public boolean sendMessage(
            String phoneNumber,
            String message) {

        try {

            // -------------------------------------------------
            // Validate Phone
            // -------------------------------------------------

            String formattedPhone =
                    formatPhoneNumber(
                            phoneNumber);


            if (formattedPhone.isEmpty()) {

                System.err.println(
                        "❌ Invalid WhatsApp phone number");

                return false;
            }


            // -------------------------------------------------
            // Validate Message
            // -------------------------------------------------

            if (!isNotBlank(message)) {

                System.err.println(
                        "❌ WhatsApp message is empty");

                return false;
            }


            System.out.println(
                    "================================================");

            System.out.println(
                    "📤 Sending WhatsApp message");

            System.out.println(
                    "📱 To: "
                            + formattedPhone);

            System.out.println(
                    "📝 Message: "
                            + message);

            System.out.println(
                    "================================================");


            // -------------------------------------------------
            // Validate Configuration
            // -------------------------------------------------

            if (!isWebhookConfigured()) {

                System.err.println(
                        "❌ WhatsApp API is not configured.");

                System.err.println(
                        "Check:");

                System.err.println(
                        "whatsapp.phone.number.id");

                System.err.println(
                        "whatsapp.access.token");

                System.err.println(
                        "whatsapp.admin.number");

                return false;
            }


            // -------------------------------------------------
            // Build Request Body
            // -------------------------------------------------

            Map<String, Object> requestBody =
                    new HashMap<>();


            requestBody.put(
                    "messaging_product",
                    "whatsapp");


            requestBody.put(
                    "recipient_type",
                    "individual");


            requestBody.put(
                    "to",
                    formattedPhone);


            requestBody.put(
                    "type",
                    "text");


            Map<String, Object> textObject =
                    new HashMap<>();


            textObject.put(
                    "preview_url",
                    false);


            textObject.put(
                    "body",
                    message);


            requestBody.put(
                    "text",
                    textObject);


            // -------------------------------------------------
            // Headers
            // -------------------------------------------------

            HttpHeaders headers =
                    new HttpHeaders();


            headers.setContentType(
                    MediaType.APPLICATION_JSON);


            headers.setBearerAuth(
                    accessToken);


            headers.setAccept(
                    java.util.List.of(
                            MediaType.APPLICATION_JSON));


            // -------------------------------------------------
            // API URL
            // -------------------------------------------------

            String baseUrl =
                    whatsAppApiUrl;


            if (baseUrl.endsWith("/")) {

                baseUrl =
                        baseUrl.substring(
                                0,
                                baseUrl.length() - 1);
            }


            String url =
                    baseUrl
                            + "/"
                            + phoneNumberId
                            + "/messages";


            System.out.println(
                    "🌐 WhatsApp API URL: "
                            + url);


            // -------------------------------------------------
            // HTTP Entity
            // -------------------------------------------------

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(
                            requestBody,
                            headers);


            // -------------------------------------------------
            // Call Meta API
            // -------------------------------------------------

            ResponseEntity<String> response;


            try {

                response =
                        restTemplate.exchange(

                                url,

                                HttpMethod.POST,

                                entity,

                                String.class
                        );

            } catch (HttpStatusCodeException ex) {

                System.err.println(
                        "❌ WhatsApp API returned error");

                System.err.println(
                        "HTTP Status: "
                                + ex.getStatusCode());

                System.err.println(
                        "Response: "
                                + ex.getResponseBodyAsString());

                return false;
            }


            // -------------------------------------------------
            // Response
            // -------------------------------------------------

            HttpStatusCode statusCode =
                    response.getStatusCode();


            String responseBody =
                    response.getBody();


            System.out.println(
                    "📊 WhatsApp API Status: "
                            + statusCode.value());


            System.out.println(
                    "📦 WhatsApp API Response: "
                            + responseBody);


            // -------------------------------------------------
            // Any 2xx response is successful
            // -------------------------------------------------

            if (statusCode.is2xxSuccessful()) {

                System.out.println(
                        "✅ WhatsApp message sent successfully");

                return true;
            }


            System.err.println(
                    "❌ WhatsApp message failed");

            return false;


        } catch (Exception e) {

            System.err.println(
                    "❌ Exception while sending WhatsApp message:");

            System.err.println(
                    e.getMessage());

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // BUILD BOOKING CONFIRMATION MESSAGE
    // =========================================================

    private String buildBookingConfirmationMessage(
            Booking booking) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd-MM-yyyy");


        AdminCarsData car =
                booking.getAdminCarsData();


        return String.format(
                """
                🚗 *BOOKING CONFIRMED!* ✅

                Booking ID: #%d

                Car: %s %s

                Pickup: %s
                Dropoff: %s

                Pickup Location: %s
                Dropoff Location: %s

                Total: ₹%.2f

                Thank you for choosing our service! 💙
                """,

                booking.getId(),

                safe(car.getBrand()),

                safe(car.getName()),

                booking.getPickupDate()
                        .format(formatter),

                booking.getDropoffDate()
                        .format(formatter),

                safe(booking.getPickupLocation()),

                safe(booking.getDropoffLocation()),

                booking.getTotal()
        );
    }


    // =========================================================
    // BUILD STATUS UPDATE MESSAGE
    // =========================================================

    private String buildStatusUpdateMessage(
            Booking booking,
            String status) {

        String statusEmoji =
                getStatusEmoji(status);


        String statusMessage =
                getStatusMessage(status);


        AdminCarsData car =
                booking.getAdminCarsData();


        return String.format(
                """
                %s *BOOKING STATUS UPDATE*

                Booking ID: #%d

                Status: %s

                Car: %s %s

                %s

                Thank you for choosing our service. 💙
                """,

                statusEmoji,

                booking.getId(),

                status,

                safe(car.getBrand()),

                safe(car.getName()),

                statusMessage
        );
    }


    // =========================================================
    // STATUS EMOJI
    // =========================================================

    private String getStatusEmoji(
            String status) {

        if (status == null) {

            return "📋";
        }


        switch (status.toUpperCase()) {

            case "CONFIRMED":
                return "✅";

            case "REJECTED":
                return "❌";

            case "PENDING":
                return "⏳";

            case "WHATSAPP_PENDING":
                return "⏳";

            case "CANCELLED":
                return "📅";

            default:
                return "📋";
        }
    }


    // =========================================================
    // STATUS MESSAGE
    // =========================================================

    private String getStatusMessage(
            String status) {

        if (status == null) {

            return "Your booking status has been updated.";
        }


        switch (status.toUpperCase()) {

            case "CONFIRMED":

                return "🎉 Your booking has been confirmed! "
                        + "We look forward to serving you.";


            case "REJECTED":

                return "😔 We regret to inform you "
                        + "that your booking has been rejected.";


            case "PENDING":

                return "⏳ Your booking is still pending review. "
                        + "We'll update you soon.";


            case "WHATSAPP_PENDING":

                return "⏳ Your booking request has been "
                        + "received and is waiting for admin approval.";


            case "CANCELLED":

                return "📅 Your booking has been cancelled "
                        + "as requested.";


            default:

                return "Your booking status has been updated to: "
                        + status;
        }
    }


    // =========================================================
    // FORMAT PHONE NUMBER
    // =========================================================

    private String formatPhoneNumber(
            String phoneNumber) {

        if (!isNotBlank(phoneNumber)) {

            return "";
        }


        String cleaned =
                phoneNumber.replaceAll(
                        "[^0-9]",
                        "");


        /*
         * India:
         *
         * 9876543210
         *
         * becomes:
         *
         * 919876543210
         */

        if (cleaned.length() == 10) {

            cleaned =
                    "91" + cleaned;
        }


        /*
         * If already starts with 91,
         * keep it unchanged.
         */

        return cleaned;
    }


    // =========================================================
    // STRING VALIDATION
    // =========================================================

    private boolean isNotBlank(
            String value) {

        return value != null
                && !value.trim().isEmpty();
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value) {

        return value == null
                ? ""
                : value;
    }


    // =========================================================
    // ESCAPE JSON
    // =========================================================

    private String escapeJson(
            String value) {

        if (value == null) {

            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}