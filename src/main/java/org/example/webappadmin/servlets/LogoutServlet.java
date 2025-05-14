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

@WebServlet(name = "logoutServlet", value = "/logout")
public class LogoutServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LogoutServlet.class.getName());

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

            // Always invalidate session and redirect to login
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            LOGGER.warning("Error during logout: " + e.getMessage());
            // Even if the API call fails, invalidate the session and redirect to login
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
} 