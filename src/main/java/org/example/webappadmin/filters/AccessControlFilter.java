package org.example.webappadmin.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filter for controlling access to protected resources.
 * Redirects unauthenticated users to the login page.
 *
 * @author Juan Carlos
 */
public class AccessControlFilter implements Filter {

    /** List of paths that are accessible without authentication */
    private static final List<String> PUBLIC_PATHS = Arrays.asList("/login", "/users", "/users/edit/*", "/users/create", "/logout");

    /**
     * Filters requests to check authentication status.
     * Allows access to public paths and authenticated users.
     * Redirects unauthenticated users to the login page.
     *
     * @param request the servlet request
     * @param response the servlet response
     * @param chain the filter chain
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        boolean isPublicPath = PUBLIC_PATHS.stream().anyMatch(path -> requestPath.startsWith(path));

        if (isPublicPath) {
            chain.doFilter(request, response);
            return;
        }

        boolean isLoggedIn = (session != null && session.getAttribute("name") != null);

        if (!isLoggedIn) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Called by the web container to indicate to a filter that it is being taken out of service.
     */
    @Override
    public void destroy() {
    }
} 