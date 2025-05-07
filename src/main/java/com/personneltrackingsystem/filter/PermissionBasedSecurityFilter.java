package com.personneltrackingsystem.filter;

import com.personneltrackingsystem.entity.Role;
import com.personneltrackingsystem.entity.User;
import com.personneltrackingsystem.service.PermissionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PermissionBasedSecurityFilter extends OncePerRequestFilter {

    private final PermissionService permissionService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Skip filtering for authentication endpoints
        if (requestURI.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the resource name from the request path
        String resourceName = extractResourceName(requestURI);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            Role role = user.getRole();

            // Check if user has the required permission
            if (!permissionService.hasPermission(role, resourceName, method, requestURI)) {
                throw new AccessDeniedException("You don't have permission to access this resource");
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractResourceName(String requestURI) {
        // Remove leading slash and api prefix if present
        String path = requestURI.startsWith("/api/") ? requestURI.substring(5) : requestURI.substring(1);
        
        // Get the first segment of the path which typically represents the resource
        int slashIndex = path.indexOf('/');
        return slashIndex > 0 ? path.substring(0, slashIndex) : path;
    }
} 