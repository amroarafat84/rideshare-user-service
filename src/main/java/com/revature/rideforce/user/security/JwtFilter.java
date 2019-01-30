package com.revature.rideforce.user.security;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.revature.rideforce.user.services.CognitoService;

/**
 * JWT filter for getting tokens and setting the SecurityContextHolder, setting a user as logged in<p>
 * <strong>Member Variables</strong><br>
 * {@linkplain LoginTokenProvider} tokenProvider
 * @author clpeng
 * @since Iteration1 10/01/2018
 *
 */
@Service
public class JwtFilter extends OncePerRequestFilter {
	@Autowired
	private CognitoService cs;
	@Autowired
    private LoginTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
    	// Authenticate the old way
        String token = tokenProvider.extractToken(request);
        if (token != null) {
            Authentication auth = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        // Get the authorization header
        Optional.ofNullable(request.getHeader("Authorization"))
        // Make sure it starts with the 'Bearer' string
        .filter(a -> a.startsWith("Bearer "))
        // Remove the 'Bearer' portion
        .map(a -> a.substring(7))
        // Attempt to authenticate the token
        .map(cs::authenticate)
        // If authenticated set the security context
        .ifPresent(SecurityContextHolder.getContext()::setAuthentication);
        
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}