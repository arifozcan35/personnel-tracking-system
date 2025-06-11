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

        if (requestURI.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String resourceName = extractResourceName(requestURI);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            Role role = user.getRole();

            if (!permissionService.hasPermission(role, resourceName, method, requestURI)) {
                throw new AccessDeniedException("You don't have permission to access this resource");
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractResourceName(String requestURI) {
        String path = requestURI.startsWith("/api/") ? requestURI.substring(5) : requestURI.substring(1);

        int slashIndex = path.indexOf('/');
        return slashIndex > 0 ? path.substring(0, slashIndex) : path;
    }
}  