package org.example.webappadmin.utils;

/**
 * Helper class containing application-wide constants and configuration values.
 *
 * @author Juan Carlos
 */
public class AppHelper {
    /** Base URL for the API server */
    public static final String BASE_URL = "http://54.82.86.20:3000";
    
    /** Endpoint for user authentication */
    public static final String LOGIN_ENDPOINT = BASE_URL + "/auth/login";
    /** Endpoint for user management */
    public static final String USERS_ENDPOINT = BASE_URL + "/users";
    /** Endpoint for password reset requests */
    public static final String PASSWORD_RESET_ENDPOINT = BASE_URL + "/users/request-password-reset";

    /** Error message for network connectivity issues */
    public static final String ERROR_NETWORK = "Unable to connect to the server. Please try again later.";
    /** Error message for server-side errors */
    public static final String ERROR_SERVER = "An unexpected error occurred. Please try again later.";
    /** Error message for unauthorized access attempts */
    public static final String ERROR_UNAUTHORIZED = "Access denied. Admin privileges required.";
    /** Error message for invalid login credentials */
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid username/email or password.";
}