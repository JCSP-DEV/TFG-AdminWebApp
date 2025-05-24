package org.example.webappadmin.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.webappadmin.models.User;
import org.example.webappadmin.utils.ApiController;
import org.example.webappadmin.utils.AppHelper;
import org.example.webappadmin.utils.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet handling user management operations including listing, deleting, and password reset.
 *
 * @author Juan Carlos
 */
@WebServlet(name = "usersServlet", value = "/users/*")
public class UsersServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UsersServlet.class.getName());

    /**
     * Handles GET requests to list all users.
     * Redirects to login page if unauthorized or network error occurs.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String apiResponse = ApiController.makeGetRequest(AppHelper.USERS_ENDPOINT, request.getSession());
            if (apiResponse == null || apiResponse.contains("401") || apiResponse.contains("403")) {
                request.getSession().setAttribute("error", AppHelper.ERROR_UNAUTHORIZED);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            
            List<User> users = JsonUtil.fromJsonToList(apiResponse, User.class);

            request.setAttribute("users", users);
            
            request.getRequestDispatcher("/WEB-INF/users.jsp").forward(request, response);
            
        } catch (Exception e) {
            if (e.getMessage().equals(AppHelper.ERROR_NETWORK)) {
                request.getSession().setAttribute("error", AppHelper.ERROR_NETWORK);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            request.setAttribute("error", AppHelper.ERROR_SERVER);
            request.getRequestDispatcher("/WEB-INF/users.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests for user management operations.
     * Supports user deletion and password reset operations.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            return;
        }

        if (pathInfo.startsWith("/delete/")) {
            handleDeleteUser(request, response, pathInfo);
        } else if (pathInfo.startsWith("/reset-password/")) {
            handlePasswordReset(request, response, pathInfo);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
        }
    }

    /**
     * Handles user deletion requests.
     * Redirects to login page if unauthorized or network error occurs.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param pathInfo the path information containing the user ID
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        String userId = pathInfo.substring("/delete/".length());

        try {
            String deleteUrl = AppHelper.USERS_ENDPOINT + "/delete/" + userId;
            String apiResponse = ApiController.makePostRequest(deleteUrl, "{}", request.getSession());

            if (apiResponse == null || apiResponse.contains("401") || apiResponse.contains("403")) {
                request.getSession().setAttribute("error", AppHelper.ERROR_UNAUTHORIZED);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String message = JsonUtil.getFieldAsString(apiResponse, "message");
            if (message != null && message.contains("successfully")) {
                response.sendRedirect(request.getContextPath() + "/users");
            } else {
                throw new Exception(message != null ? message : "Failed to delete user");
            }
        } catch (Exception e) {
            if (e.getMessage().equals(AppHelper.ERROR_NETWORK)) {
                request.getSession().setAttribute("error", AppHelper.ERROR_NETWORK);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/users.jsp").forward(request, response);
        }
    }

    /**
     * Handles password reset requests for users.
     * Redirects to login page if unauthorized or network error occurs.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param pathInfo the path information containing the user ID
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void handlePasswordReset(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        String userId = pathInfo.substring("/reset-password/".length());

        try {
            User user = new User();
            user.setId(Long.parseLong(userId));
            user.setUsername(request.getParameter("username"));
            user.setEmail(request.getParameter("email"));
            user.setRole(request.getParameter("role"));
            user.setVerified(Boolean.parseBoolean(request.getParameter("verified")));
            
            String apiResponse = ApiController.makePostRequest(
                AppHelper.PASSWORD_RESET_ENDPOINT, 
                JsonUtil.toJson(user), 
                request.getSession()
            );

            if (apiResponse == null || apiResponse.contains("401") || apiResponse.contains("403")) {
                request.getSession().setAttribute("error", AppHelper.ERROR_UNAUTHORIZED);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String message = JsonUtil.getFieldAsString(apiResponse, "message");
            if (message != null && message.contains("sent")) {
                request.getSession().setAttribute("success", "Password reset email sent successfully");
                response.sendRedirect(request.getContextPath() + "/users");
            } else {
                throw new Exception(message != null ? message : "Failed to send password reset email");
            }
        } catch (Exception e) {
            if (e.getMessage().equals(AppHelper.ERROR_NETWORK)) {
                request.getSession().setAttribute("error", AppHelper.ERROR_NETWORK);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/users.jsp").forward(request, response);
        }
    }
} 