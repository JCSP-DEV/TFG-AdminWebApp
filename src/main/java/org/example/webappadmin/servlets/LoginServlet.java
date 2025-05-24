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

/**
 * Servlet handling user authentication operations.
 * Supports login with username/email and password.
 *
 * @author Juan Carlos
 */
@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    /**
     * Handles GET requests to display the login form.
     * Invalidates any existing session before displaying the form.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }

    /**
     * Handles POST requests for user authentication.
     * Supports login with either username or email.
     * Redirects to users page on successful admin login.
     *
     * @param request the HTTP request containing login credentials
     * @param response the HTTP response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameOrEmail = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            User loginRequest = new User();
            if (usernameOrEmail.contains("@")) {
                loginRequest.setEmail(usernameOrEmail);
            } else {
                loginRequest.setUsername(usernameOrEmail);
            }
            loginRequest.setPassword(password);

            String jsonPayload = JsonUtil.toJson(loginRequest);

            String apiResponse = ApiController.makePostRequest(AppHelper.LOGIN_ENDPOINT, jsonPayload, request.getSession());

            String message = JsonUtil.getFieldAsString(apiResponse, "message");

            if (message != null && message.contains("successful")) {
                User user = JsonUtil.getFieldAsType(apiResponse, "user", User.class);

                if (user != null && "ADMIN".equals(user.getRole())) {
                    HttpSession session = request.getSession();
                    session.setAttribute("name", user.getUsername());
                    response.sendRedirect(request.getContextPath() + "/users");
                } else {
                    request.setAttribute("error", AppHelper.ERROR_UNAUTHORIZED);
                    request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
                }
            } else {
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