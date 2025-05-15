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

@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        // Clear all session attributes
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameOrEmail = request.getParameter("username");
        String password = request.getParameter("password");


        try {
            // Create login request payload
            User loginRequest = new User();
            if (usernameOrEmail.contains("@")) {
                loginRequest.setEmail(usernameOrEmail);
            } else {
                loginRequest.setUsername(usernameOrEmail);
            }
            loginRequest.setPassword(password);

            // Convert to JSON
            String jsonPayload = JsonUtil.toJson(loginRequest);

            // Make API call
            String apiResponse = ApiController.makePostRequest(AppHelper.LOGIN_ENDPOINT, jsonPayload, request.getSession());

            // Check response
            String message = JsonUtil.getFieldAsString(apiResponse, "message");

            if (message != null && message.contains("successful")) {
                // Get user from response
                User user = JsonUtil.getFieldAsType(apiResponse, "user", User.class);

                // Check if user is admin
                if (user != null && "ADMIN".equals(user.getRole())) {
                    HttpSession session = request.getSession();
                    // Store the token in the session
                    session.setAttribute("name", user.getUsername());
                    response.sendRedirect(request.getContextPath() + "/users");
                } else {
                    request.setAttribute("error", AppHelper.ERROR_UNAUTHORIZED);
                    request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
                }
            } else {
                // Login failed
                request.setAttribute("error", message != null ? message : AppHelper.ERROR_INVALID_CREDENTIALS);
                request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            String errorMessage;
            if (e instanceof java.net.UnknownHostException || e instanceof java.net.ConnectException) {
                errorMessage = AppHelper.ERROR_NETWORK;
            } else {
                errorMessage = AppHelper.ERROR_SERVER;
            }
            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
        }
    }
} 