package org.example.webappadmin.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.webappadmin.utils.ApiController;
import org.example.webappadmin.utils.AppHelper;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet handling user logout operations.
 * Invalidates the session and redirects to the login page.
 *
 * @author Juan Carlos
 */
@WebServlet(name = "logoutServlet", value = "/logout")
public class LogoutServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LogoutServlet.class.getName());

    /**
     * Handles POST requests to log out a user.
     * Invalidates the session and redirects to the login page.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOGGER.info("Processing logout request");
        try {
            String apiResponse = ApiController.makePostRequest(
                AppHelper.BASE_URL + "/auth/logout",
                "{}",
                request.getSession()
            );

            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            LOGGER.warning("Error during logout: " + e.getMessage());
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
} 