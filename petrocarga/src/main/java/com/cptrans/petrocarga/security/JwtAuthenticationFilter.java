package com.cptrans.petrocarga.security;

import java.io.IOException;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull
            HttpServletRequest request,
            @NonNull
            HttpServletResponse response,
            @NonNull
            FilterChain filterChain) throws ServletException, IOException {

        try{
             final String token = resolveToken(request);
        
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                final String email = jwtService.getEmailDoToken(token);
                if(email != null && !email.isEmpty() && jwtService.validarToken(token)){
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    UUID id = jwtService.getIdDoToken(token);
                    UserAuthenticated userAuthenticated = new UserAuthenticated(id, userDetails);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userAuthenticated, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }catch (Exception e) {
            System.out.println("Erro ao autenticar o usuário: " + e.getMessage());
            System.out.println("UserAuthenticated: " + (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
    private String resolveToken(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("auth-token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}
