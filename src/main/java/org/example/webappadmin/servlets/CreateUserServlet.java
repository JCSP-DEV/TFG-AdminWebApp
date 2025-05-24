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

/**
 * Servlet handling user creation operations.
 *
 * @author Juan Carlos
 */
@WebServlet(name = "createUserServlet", value = "/users/create")
public class CreateUserServlet extends HttpServlet {

    /**
     * Handles GET requests to display the user creation form.
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
            String apiResponse = ApiController.makeGetRequest(AppHelper.BASE_URL + "/auth/check-session", request.getSession());
            
            if (apiResponse == null || apiResponse.contains("401") || apiResponse.contains("403")) {
                request.getSession().setAttribute("error", AppHelper.ERROR_UNAUTHORIZED);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            request.getRequestDispatcher("/WEB-INF/create.jsp").forward(request, response);
            
        } catch (Exception e) {
            if (e.getMessage().equals(AppHelper.ERROR_NETWORK)) {
                request.getSession().setAttribute("error", AppHelper.ERROR_NETWORK);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            request.getSession().setAttribute("error", AppHelper.ERROR_SERVER);
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    /**
     * Handles POST requests to create a new user.
     * Redirects to login page if unauthorized or network error occurs.
     * On success, redirects to the users list page.
     *
     * @param request the HTTP request containing user details
     * @param response the HTTP response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            User user = new User();
            user.setUsername(request.getParameter("username"));
            user.setEmail(request.getParameter("email"));
            user.setPassword(request.getParameter("password"));
            user.setRole(request.getParameter("role"));
            user.setVerified(false);

            String jsonPayload = JsonUtil.toJson(user);

            String apiResponse = ApiController.makePostRequest(
                AppHelper.USERS_ENDPOINT + "/create", 
                jsonPayload, 
                request.getSession()
            );

            if (apiResponse == null || apiResponse.contains("401") || apiResponse.contains("403")) {
                request.getSession().setAttribute("error", AppHelper.ERROR_UNAUTHORIZED);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String message = JsonUtil.getFieldAsString(apiResponse, "message");
            if (message != null && message.contains("successfully")) {
                response.sendRedirect(request.getContextPath() + "/users");
            } else {
                throw new Exception(message != null ? message : "Failed to create user");
            }
        } catch (Exception e) {
            if (e.getMessage().equals(AppHelper.ERROR_NETWORK)) {
                request.getSession().setAttribute("error", AppHelper.ERROR_NETWORK);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/create.jsp").forward(request, response);
        }
    }
} 