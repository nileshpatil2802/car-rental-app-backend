//package com.My_Car_Rental_Application.dto;
//
//public class ApiResponse {
//
//    private boolean success;
//    private String message;
//
//    public ApiResponse() {
//    }
//
//    public ApiResponse(
//            boolean success,
//            String message
//    ) {
//        this.success = success;
//        this.message = message;
//    }
//
//    public static ApiResponse success(String message) {
//        return new ApiResponse(true, message);
//    }
//
//    public static ApiResponse error(String message) {
//        return new ApiResponse(false, message);
//    }
//
//    public boolean isSuccess() {
//        return success;
//    }
//
//    public void setSuccess(boolean success) {
//        this.success = success;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//}
package com.My_Car_Rental_Application.dto;

public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;
    private String errorCode;
    private long timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }

    public ApiResponse(boolean success, String message, Object data) {
        this(success, message);
        this.data = data;
    }

    public static ApiResponse success(String message) {
        return new ApiResponse(true, message);
    }

    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message);
    }

    public static ApiResponse error(String message, String errorCode) {
        ApiResponse response = new ApiResponse(false, message);
        response.setErrorCode(errorCode);
        return response;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
