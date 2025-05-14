package org.example.webappadmin.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AccessControlFilter implements Filter {

    private static final List<String> PUBLIC_PATHS = Arrays.asList("/login", "/users", "/users/edit/*", "/users/create", "/logout");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Check if the path is public
        boolean isPublicPath = PUBLIC_PATHS.stream().anyMatch(path -> requestPath.startsWith(path));

        // Allow access to public paths
        if (isPublicPath) {
            chain.doFilter(request, response);
            return;
        }

        // Check if user is logged in
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (!isLoggedIn) {
            // Redirect to login page if not logged in
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // Continue with the request if user is logged in
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
} 