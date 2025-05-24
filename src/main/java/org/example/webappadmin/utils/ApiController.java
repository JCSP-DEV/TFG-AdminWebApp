package org.example.webappadmin.utils;

import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Logger;

/**
 * Utility class for handling HTTP API requests with session management and error handling.
 *
 * @author Juan Carlos
 */
public class ApiController {
    private static final Logger LOGGER = Logger.getLogger(ApiController.class.getName());
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_APPLICATION_JSON = "application/json";
    private static final String SESSION_COOKIE_MANAGER = "cookieManager";

    /**
     * Makes a POST request to the specified URL with JSON payload and session management.
     *
     * @param url the target URL for the POST request
     * @param jsonPayload the JSON payload to send in the request body
     * @param session the HTTP session for maintaining cookies and authentication
     * @return the response body as a string
     * @throws Exception if there are network issues or server errors
     */
    public static String makePostRequest(String url, String jsonPayload, HttpSession session) throws Exception {
        try {
            LOGGER.info("Making POST request to: " + url);
            LOGGER.info("Request payload: " + jsonPayload);

            java.net.CookieManager cookieManager = getCookieManager(session);
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(TIMEOUT)
                    .cookieHandler(cookieManager)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(HEADER_CONTENT_TYPE, HEADER_APPLICATION_JSON)
                    .header("Authorization", "Bearer " + session.getAttribute("token"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .timeout(TIMEOUT)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            int statusCode = response.statusCode();
            LOGGER.info("Response code: " + statusCode);
            LOGGER.info("Response body: " + response.body());

            if (statusCode == 401 || statusCode == 403) {
                LOGGER.warning("Unauthorized access attempt");
                return String.valueOf(statusCode);
            }

            if (statusCode > 400) {
                LOGGER.warning("Server returned error code: " + statusCode);
                throw new Exception(AppHelper.ERROR_SERVER);
            }

            return response.body();

        } catch (Exception e) {
            LOGGER.severe("Error making POST request: " + e.getMessage());
            LOGGER.severe("Stack trace: " + e);
            if (e instanceof java.net.UnknownHostException || 
                e instanceof java.net.ConnectException ||
                e instanceof java.net.http.HttpConnectTimeoutException) {
                throw new Exception(AppHelper.ERROR_NETWORK);
            }
            throw new Exception(AppHelper.ERROR_SERVER);
        }
    }

    /**
     * Makes a GET request to the specified URL with session management.
     *
     * @param url the target URL for the GET request
     * @param session the HTTP session for maintaining cookies and authentication
     * @return the response body as a string
     * @throws Exception if there are network issues or server errors
     */
    public static String makeGetRequest(String url, HttpSession session) throws Exception {
        try {
            LOGGER.info("Making GET request to: " + url);

            java.net.CookieManager cookieManager = getCookieManager(session);
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(TIMEOUT)
                    .cookieHandler(cookieManager)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(HEADER_CONTENT_TYPE, HEADER_APPLICATION_JSON)
                    .header("Authorization", "Bearer " + session.getAttribute("token"))
                    .GET()
                    .timeout(TIMEOUT)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            int statusCode = response.statusCode();
            LOGGER.info("Response code: " + statusCode);
            LOGGER.info("Response body: " + response.body());

            if (statusCode == 401 || statusCode == 403) {
                LOGGER.warning("Unauthorized access attempt");
                return String.valueOf(statusCode);
            }

            if (statusCode > 400) {
                LOGGER.warning("Server returned error code: " + statusCode);
                throw new Exception(AppHelper.ERROR_SERVER);
            }

            return response.body();

        } catch (Exception e) {
            LOGGER.severe("Error making GET request: " + e.getMessage());
            LOGGER.severe("Stack trace: " + e);
            if (e instanceof java.net.UnknownHostException || 
                e instanceof java.net.ConnectException ||
                e instanceof java.net.http.HttpConnectTimeoutException) {
                throw new Exception(AppHelper.ERROR_NETWORK);
            }
            throw new Exception(AppHelper.ERROR_SERVER);
        }
    }

    /**
     * Gets or creates a CookieManager for the current session.
     *
     * @param session the HTTP session to store the CookieManager
     * @return the CookieManager instance for the session
     */
    private static java.net.CookieManager getCookieManager(HttpSession session) {
        java.net.CookieManager cookieManager = (java.net.CookieManager) session.getAttribute(SESSION_COOKIE_MANAGER);
        if (cookieManager == null) {
            cookieManager = new java.net.CookieManager();
            session.setAttribute(SESSION_COOKIE_MANAGER, cookieManager);
        }
        return cookieManager;
    }

    /**
     * Makes a PATCH request to the specified URL with JSON payload and session management.
     *
     * @param url the target URL for the PATCH request
     * @param jsonPayload the JSON payload to send in the request body
     * @param session the HTTP session for maintaining cookies and authentication
     * @return the response body as a string
     * @throws Exception if there are network issues or server errors
     */
    public static String makePatchRequest(String url, String jsonPayload, HttpSession session) throws Exception {
        try {
            LOGGER.info("Making PATCH request to: " + url);
            LOGGER.info("Request payload: " + jsonPayload);

            // Get or create CookieManager for this session
            java.net.CookieManager cookieManager = getCookieManager(session);
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(TIMEOUT)
                    .cookieHandler(cookieManager)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(HEADER_CONTENT_TYPE, HEADER_APPLICATION_JSON)
                    .header("Authorization", "Bearer " + session.getAttribute("token"))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .timeout(TIMEOUT)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            int statusCode = response.statusCode();
            LOGGER.info("Response code: " + statusCode);
            LOGGER.info("Response body: " + response.body());

            if (statusCode == 401 || statusCode == 403) {
                LOGGER.warning("Unauthorized access attempt");
                return String.valueOf(statusCode);
            }

            if (statusCode > 400) {
                LOGGER.warning("Server returned error code: " + statusCode);
                throw new Exception(AppHelper.ERROR_SERVER);
            }

            return response.body();

        } catch (Exception e) {
            LOGGER.severe("Error making PATCH request: " + e.getMessage());
            LOGGER.severe("Stack trace: " + e);
            if (e instanceof java.net.UnknownHostException || 
                e instanceof java.net.ConnectException ||
                e instanceof java.net.http.HttpConnectTimeoutException) {
                throw new Exception(AppHelper.ERROR_NETWORK);
            }
            throw new Exception(AppHelper.ERROR_SERVER);
        }
    }
}