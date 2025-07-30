package com.sleepystack.bankingapp.filter;


import com.sleepystack.bankingapp.service.JsonWebTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JsonWebTokenFilter extends OncePerRequestFilter {
    private final JsonWebTokenService jsonWebTokenService;

    @Autowired
    public JsonWebTokenFilter(JsonWebTokenService jsonWebTokenService) {
        this.jsonWebTokenService = jsonWebTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String AuthorizationHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String userEmail = null;
        if (AuthorizationHeader != null && AuthorizationHeader.startsWith("Bearer ")) {
            jwtToken = AuthorizationHeader.substring(7);
            try {
                userEmail = jsonWebTokenService.extractEmailFromToken(jwtToken);
            } catch (Exception e) {
                // Handle token parsing error
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }
    }
}
