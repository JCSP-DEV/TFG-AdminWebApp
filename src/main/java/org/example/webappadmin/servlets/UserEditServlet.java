package org.example.webappadmin.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webappadmin.models.User;
import org.example.webappadmin.utils.ApiController;
import org.example.webappadmin.utils.AppHelper;
import org.example.webappadmin.utils.JsonUtil;

import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/users/edit/*")
public class UserEditServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UserEditServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        // Get user ID from URL
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/users");
            return;
        }

        String userId = pathInfo.substring(1); // Remove leading slash
        LOGGER.info("Attempting to load user with ID: " + userId);

        try {
            // Get user data from API
            String apiResponse = ApiController.makeGetRequest(AppHelper.USERS_ENDPOINT + "/" + userId, session);
            LOGGER.info("API Response received: " + apiResponse);
            
            // Check for unauthorized access
            if (apiResponse.equals("401") || apiResponse.equals("403")) {
                LOGGER.warning("Unauthorized access attempt for user ID: " + userId);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Check if response is empty or invalid
            if (apiResponse == null || apiResponse.trim().isEmpty()) {
                LOGGER.warning("Empty response received from API for user ID: " + userId);
                request.setAttribute("error", "No data received from server");
                request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
                return;
            }

            // Parse user data
            User user = JsonUtil.fromJson(apiResponse, User.class);
            LOGGER.info("Parsed user data: " + (user != null ? user.toString() : "null"));
            
            // Validate user data
            if (user == null) {
                LOGGER.warning("User data is null for ID: " + userId);
                request.setAttribute("error", "User not found");
                request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
                return;
            }

            // Validate user ID
            if (user.getId() == null) {
                LOGGER.warning("User ID is null for user: " + user.getUsername());
                request.setAttribute("error", "Invalid user data: ID is missing");
                request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
                return;
            }

            // Ensure required fields are not null
            if (user.getUsername() == null) user.setUsername("");
            if (user.getEmail() == null) user.setEmail("");
            if (user.getRole() == null) user.setRole("USER");
            
            request.setAttribute("user", user);
            
            // Forward to edit page
            request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.severe("Error loading user data: " + e.getMessage());
            LOGGER.severe("Stack trace: " + e.toString());
            
            String errorMessage;
            if (e.getMessage().equals(AppHelper.ERROR_NETWORK)) {
                request.getSession().setAttribute("error", AppHelper.ERROR_NETWORK);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            } else if (e instanceof com.google.gson.JsonSyntaxException) {
                LOGGER.severe("JSON parsing error: " + e.getMessage());
                errorMessage = "Invalid user data format received from server";
            } else {
                LOGGER.severe("Unexpected error: " + e.getClass().getName() + " - " + e.getMessage());
                errorMessage = "Error loading user data. Please try again.";
            }
            
            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        try {
            // Get form data
            String userId = request.getParameter("id");
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String role = request.getParameter("role");
            String password = request.getParameter("password");
            String verified = request.getParameter("verified");

            // Create User object
            User user = new User();
            user.setId(Long.parseLong(userId));
            user.setUsername(username);
            user.setEmail(email);
            user.setRole(role != null ? role : "USER");
            user.setVerified(verified != null);
            
            // Only set password if provided
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(password);
            }

            // Convert to JSON
            String jsonPayload = JsonUtil.toJson(user);

            // Send update request to API
            String apiResponse = ApiController.makePatchRequest(
                AppHelper.USERS_ENDPOINT + "/update/" + userId,
                jsonPayload,
                session
            );

            // Check for unauthorized access
            if (apiResponse.equals("401") || apiResponse.equals("403")) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Check response message
            String message = JsonUtil.getFieldAsString(apiResponse, "message");
            if (message != null) {
                if (message.contains("successfully")) {
                    // Success case
                    response.sendRedirect(request.getContextPath() + "/users");
                    return;
                } else if (message.contains("already exists")) {
                    // Handle duplicate username/email
                    request.setAttribute("error", message);
                    request.setAttribute("user", user); // Keep the form data
                    request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
                    return;
                } else if (message.contains("Failed to update")) {
                    // Handle general update failure
                    request.setAttribute("error", "Failed to update user. Please try again.");
                    request.setAttribute("user", user); // Keep the form data
                    request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
                    return;
                }
            }
            
            // Default error case
            request.setAttribute("error", AppHelper.ERROR_SERVER);
            request.setAttribute("user", user); // Keep the form data
            request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.severe("Error updating user: " + e.getMessage());
            LOGGER.severe("Stack trace: " + e.toString());
            
            String errorMessage;
            if (e.getMessage().equals(AppHelper.ERROR_NETWORK)) {
                request.getSession().setAttribute("error", AppHelper.ERROR_NETWORK);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            } else {
                errorMessage = "Error updating user. Please try again.";
            }
            
            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
        }
    }
} 