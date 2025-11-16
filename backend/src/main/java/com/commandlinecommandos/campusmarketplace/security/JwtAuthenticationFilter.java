package com.commandlinecommandos.campusmarketplace.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String username = null;
        
        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extract JWT token
        jwt = authHeader.substring(7);
        
        try {
            // Extract username from JWT
            username = jwtUtil.extractUsername(jwt);
            
            // If username is valid and no authentication is set in SecurityContext
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Validate token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    
                    // Set authentication details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("JWT authentication successful for user: {}", username);
                } else {
                    logger.warn("JWT token validation failed for user: {} - token is invalid or expired", username);
                }
            }
        } catch (UsernameNotFoundException e) {
            // User not found in database - log at WARN level for visibility
            String usernameForLog = username != null ? username : "unknown";
            logger.warn("JWT token validation failed: User '{}' not found in database. Token may be valid but user was deleted.", usernameForLog, e);
        } catch (Exception e) {
            // Log other exceptions (JWT parsing errors, etc.) at WARN level
            String usernameForLog = username != null ? username : "unknown";
            logger.warn("JWT token validation failed for user '{}': {}", usernameForLog, e.getMessage(), e);
        }
        
        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
