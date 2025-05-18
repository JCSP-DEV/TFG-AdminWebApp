package org.example.webappadmin.utils;

public class AppHelper {
    public static final String BASE_URL = "http://54.82.86.20:3000";
    
    public static final String LOGIN_ENDPOINT = BASE_URL + "/auth/login";
    public static final String USERS_ENDPOINT = BASE_URL + "/users";
    public static final String PASSWORD_RESET_ENDPOINT = BASE_URL + "/users/request-password-reset";

    
    public static final String ERROR_NETWORK = "Unable to connect to the server. Please try again later.";
    public static final String ERROR_SERVER = "An unexpected error occurred. Please try again later.";
    public static final String ERROR_UNAUTHORIZED = "Access denied. Admin privileges required.";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid username/email or password.";
}