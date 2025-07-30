package com.sleepystack.bankingapp.filter;


import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.service.JsonWebTokenService;
import com.sleepystack.bankingapp.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JsonWebTokenFilter extends OncePerRequestFilter {
    private final JsonWebTokenService jsonWebTokenService;
    private final UserService userService;

    @Autowired
    public JsonWebTokenFilter(JsonWebTokenService jsonWebTokenService, UserService userService) {
        this.jsonWebTokenService = jsonWebTokenService;
        this.userService = userService;
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
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if(jsonWebTokenService.validateToken(jwtToken, userEmail)) {
                User user = userService.getUserByEmail(userEmail);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
                        UsernamePasswordAuthenticationToken(user, null, userService.getAuthorities(user.getPublicIdentifier()));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        } else {
            request.setAttribute("userEmail", "anonymous");
        }
        filterChain.doFilter(request, response);
    }
}
