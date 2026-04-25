package com.letroupeau.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Component("customCorsFilter")
public class CorsFilter implements Filter {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String origin = httpRequest.getHeader("Origin");
        
        System.out.println("🔍 CORS Filter - Origin: " + origin);
        System.out.println("🔍 CORS Filter - Allowed Origins: " + allowedOrigins);

        // Vérifier si l'origine est autorisée
        if (origin != null && Arrays.asList(allowedOrigins.split(",")).contains(origin.trim())) {
            System.out.println("✅ Origin autorisée: " + origin);
            
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,PATCH,OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization,Content-Type,Accept");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
            httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
        } else {
            System.out.println("❌ Origin non autorisée: " + origin);
        }

        // Répondre aux requêtes OPTIONS
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}