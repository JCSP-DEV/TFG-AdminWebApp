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

@WebServlet(name = "usersServlet", value = "/users/*")
public class UsersServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UsersServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get users from API
            String apiResponse = ApiController.makeGetRequest(AppHelper.USERS_ENDPOINT, request.getSession());

            if (apiResponse == null || apiResponse.contains("401") || apiResponse.contains("403")) {
                request.getSession().setAttribute("error", AppHelper.ERROR_UNAUTHORIZED);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            
            List<User> users = JsonUtil.fromJsonToList(apiResponse, User.class);

            // Set users in request attribute
            request.setAttribute("users", users);
            
            // Forward to JSP
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Handle user deletion
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || !pathInfo.startsWith("/delete/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            return;
        }

        String userId = pathInfo.substring("/delete/".length());

        try {
            // Make delete request to API
            String deleteUrl = AppHelper.USERS_ENDPOINT + "/delete/" + userId;
            String apiResponse = ApiController.makePostRequest(deleteUrl, "{}", request.getSession());

            if (apiResponse == null || apiResponse.contains("401") || apiResponse.contains("403")) {
                request.getSession().setAttribute("error", AppHelper.ERROR_UNAUTHORIZED);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Check response
            String message = JsonUtil.getFieldAsString(apiResponse, "message");
            if (message != null && message.contains("successfully")) {
                // Redirect back to users page
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
} 