package com.thacbao.codeSphere.CORS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200", "http://20.42.212.224:4200/",
                        "https://20.42.212.224", "https://codesphere.id.vn")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public static CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:4200"); // allow Angular
        configuration.addAllowedOrigin("http://20.42.212.224:4200");
        configuration.addAllowedOrigin("https://20.42.212.224");
        configuration.addAllowedOrigin("https://codesphere.id.vn");
        configuration.addAllowedMethod("*"); // allow (GET, POST, PUT, DELETE, etc.)
        configuration.addAllowedHeader("*"); // allow header
        configuration.setAllowCredentials(true); // cookie

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
