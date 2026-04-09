package com.proyecto.PlayApp.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Locale;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String selectedRole = request.getParameter("roleSelection");
        if (selectedRole != null && !selectedRole.isBlank()) {
            String expectedAuthority = "ROLE_" + selectedRole.trim().toUpperCase(Locale.ROOT);
            boolean hasSelectedRole = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(expectedAuthority));

            if (!hasSelectedRole) {
                request.getSession().invalidate();
                redirectStrategy.sendRedirect(request, response, "/login?error=role");
                return;
            }
        }

        String redirectURL = request.getContextPath();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            redirectURL = "/manager/dashboard";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            redirectURL = "/shop";
        }

        response.sendRedirect(redirectURL);
    }
}
