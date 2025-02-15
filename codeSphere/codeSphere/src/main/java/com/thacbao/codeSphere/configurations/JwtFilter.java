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
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    Claims claims = null;
    String username = null;

    private static final String[] PUBLIC_PATHS = {
            "/api/v1/auth/login",
            "/api/v1/auth/signup",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/verify-account",
            "/api/v1/auth/regenerate-otp",
            "/api/v1/auth/test",
            "/api/v1/auth/set-password.*",
            "/api/v1/auth/verify-forgot-password",
    };
    private Boolean isPublicPath(String path){
        return Arrays.stream(PUBLIC_PATHS).anyMatch(path::matches);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isPublicPath(request.getServletPath())){
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
        List<String> roles = claims.get("role", List.class);
        return roles.contains("admin".toUpperCase());
    }
    public Boolean isUser(){
        List<String> roles = claims.get("role", List.class);
        return roles.contains("user".toUpperCase());
    }
    public Boolean isManager(){
        List<String> roles = claims.get("role", List.class);
        return roles.contains("manager".toUpperCase());
    }
    public Boolean isBlogger(){
        List<String> roles = claims.get("role", List.class);
        return roles.contains("blogger".toUpperCase());
    }
    public String getCurrentUsername(){
        return username;
    }
}
