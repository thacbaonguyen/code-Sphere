package com.thacbao.codeSphere.configurations;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    Claims claims = null;
    String username = null;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().matches("/api/v1/auth/login|/api/v1/auth/signup|" +
                "/api/v1/auth/forgot-password|/api/v1/auth/verify-account|/api/v1/auth/regenerate-otp|/api/v1/auth/test")){
            filterChain.doFilter(request, response);
        }
        else{
            String authorizationHeader = request.getHeader("Authorization");
            String token = null;
            if(authorizationHeader.startsWith("Bearer ")){
                token = authorizationHeader.substring(7);
                claims = jwtUtils.getClaimsFromToken(token);
                username = jwtUtils.getUsernameFromToken(token);
            }
            if(claims != null && username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                if(jwtUtils.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken authentication = new
                            UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        }
    }

    public Boolean isAdmin(){
        return "admin".equalsIgnoreCase(claims.get("role", String.class));
    }
    public Boolean isUser(){
        return "user".equalsIgnoreCase(claims.get("role", String.class));
    }
    public Boolean isManager(){
        return "manager".equalsIgnoreCase(claims.get("role", String.class));
    }
    public String getCurrentUsername(){
        return username;
    }
}
