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

/**
 * Servlet handling user editing operations.
 * Supports loading user data and updating user information.
 *
 * @author Juan Carlos
 */
@WebServlet("/users/edit/*")
public class UserEditServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UserEditServlet.class.getName());

    /**
     * Handles GET requests to load and display user data for editing.
     * Redirects to login page if unauthorized or network error occurs.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/users");
            return;
        }

        String userId = pathInfo.substring(1); // Remove leading slash
        LOGGER.info("Attempting to load user with ID: " + userId);

        try {
            String apiResponse = ApiController.makeGetRequest(AppHelper.USERS_ENDPOINT + "/" + userId, session);
            LOGGER.info("API Response received: " + apiResponse);
            
            if (apiResponse.equals("401") || apiResponse.equals("403")) {
                LOGGER.warning("Unauthorized access attempt for user ID: " + userId);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (apiResponse == null || apiResponse.trim().isEmpty()) {
                LOGGER.warning("Empty response received from API for user ID: " + userId);
                request.setAttribute("error", "No data received from server");
                request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
                return;
            }

            User user = JsonUtil.fromJson(apiResponse, User.class);
            LOGGER.info("Parsed user data: " + (user != null ? user.toString() : "null"));
            
            if (user == null) {
                LOGGER.warning("User data is null for ID: " + userId);
                request.setAttribute("error", "User not found");
                request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
                return;
            }

            if (user.getId() == null) {
                LOGGER.warning("User ID is null for user: " + user.getUsername());
                request.setAttribute("error", "Invalid user data: ID is missing");
                request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
                return;
            }

            if (user.getUsername() == null) user.setUsername("");
            if (user.getEmail() == null) user.setEmail("");
            if (user.getRole() == null) user.setRole("USER");
            
            request.setAttribute("user", user);
            
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

    /**
     * Handles POST requests to update user information.
     * Redirects to login page if unauthorized or network error occurs.
     * On success, redirects to the users list page.
     *
     * @param request the HTTP request containing updated user details
     * @param response the HTTP response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        try {
            String userId = request.getParameter("id");
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String role = request.getParameter("role");
            String password = request.getParameter("password");
            String verified = request.getParameter("verified");

            User user = new User();
            user.setId(Long.parseLong(userId));
            user.setUsername(username);
            user.setEmail(email);
            user.setRole(role != null ? role : "USER");
            user.setVerified(verified != null);
            
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(password);
            }

            String jsonPayload = JsonUtil.toJson(user);

            String apiResponse = ApiController.makePatchRequest(
                AppHelper.USERS_ENDPOINT + "/update/" + userId,
                jsonPayload,
                session
            );

            if (apiResponse.equals("401") || apiResponse.equals("403")) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String message = JsonUtil.getFieldAsString(apiResponse, "message");
            if (message != null) {
                if (message.contains("successfully")) {
                    response.sendRedirect(request.getContextPath() + "/users");
                    return;
                } else if (message.contains("already exists")) {
                    request.setAttribute("error", message);
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
                    return;
                } else if (message.contains("Failed to update")) {
                    request.setAttribute("error", "Failed to update user. Please try again.");
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/WEB-INF/edit.jsp").forward(request, response);
                    return;
                }
            }
            
            request.setAttribute("error", AppHelper.ERROR_SERVER);
            request.setAttribute("user", user);
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