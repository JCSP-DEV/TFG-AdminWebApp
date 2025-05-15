package org.example.webappadmin.utils;

public class AppHelper {
    // API Base URL
    public static final String BASE_URL = "http://54.82.86.20:3000";
    
    // API Endpoints
    public static final String LOGIN_ENDPOINT = BASE_URL + "/auth/login";
    public static final String USERS_ENDPOINT = BASE_URL + "/users";
    public static final String PASSWORD_RESET_ENDPOINT = BASE_URL + "/users/request-password-reset";
    
    // Headers
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_APPLICATION_JSON = "application/json";
    
    // Error Messages
    public static final String ERROR_NETWORK = "Unable to connect to the server. Please try again later.";
    public static final String ERROR_SERVER = "An unexpected error occurred. Please try again later.";
    public static final String ERROR_UNAUTHORIZED = "Access denied. Admin privileges required.";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid username/email or password.";
    public static final String ERROR_USER_NOT_VERIFIED = "Account not verified. Please check your email.";
} 