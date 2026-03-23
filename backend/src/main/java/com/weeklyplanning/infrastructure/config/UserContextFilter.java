package com.weeklyplanning.infrastructure.config;

import com.weeklyplanning.domain.entity.AppUser;
import com.weeklyplanning.infrastructure.repository.AppUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class UserContextFilter implements Filter {

    private final AppUserRepository appUserRepository;

    public UserContextFilter(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        if (!path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        // Allow unauthenticated access to user list and chess categories (needed for user switcher)
        if (path.equals("/api/users") || path.equals("/api/chess-categories")) {
            String userIdHeader = httpRequest.getHeader("X-User-Id");
            if (userIdHeader == null || userIdHeader.isBlank()) {
                chain.doFilter(request, response);
                return;
            }
        }

        String userIdHeader = httpRequest.getHeader("X-User-Id");
        if (userIdHeader == null || userIdHeader.isBlank()) {
            writeError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    "MISSING_USER", "X-User-Id header required");
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            writeError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    "MISSING_USER", "X-User-Id header required");
            return;
        }

        Optional<AppUser> userOpt = appUserRepository.findById(userId);
        if (userOpt.isEmpty()) {
            writeError(httpResponse, HttpServletResponse.SC_NOT_FOUND,
                    "USER_NOT_FOUND", "User not found");
            return;
        }

        try {
            UserContext.set(userOpt.get());
            chain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private void writeError(HttpServletResponse response, int status, String error, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                String.format("{\"error\": \"%s\", \"message\": \"%s\"}", error, message)
        );
    }
}
