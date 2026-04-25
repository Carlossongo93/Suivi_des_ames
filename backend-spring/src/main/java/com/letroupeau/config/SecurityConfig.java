package com.letroupeau.config;

import com.letroupeau.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permet d'utiliser @PreAuthorize dans nos contrôleurs
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Configuration CORS stricte (Axe A de l'ADD)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. Désactivation CSRF (inutile car nous utilisons des JWT et non des cookies de session)
            .csrf(csrf -> csrf.disable())
            
            // 3. Règles de routage
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Routes publiques (login)
                .requestMatchers("/error").permitAll() // <-- LA LIGNE MAGIQUE À AJOUTER
                .anyRequest().authenticated() // Toutes les autres requêtes nécessitent d'être connecté
            )
            
            // 4. Statelessness Absolu (Aucune session n'est créée sur le serveur)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 5. Providers et Filtres
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean de configuration CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // On n'accepte QUE l'URL définie dans les variables d'environnement (ex: l'URL Vercel)
        configuration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        
        // On autorise les méthodes classiques
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // On autorise l'en-tête Authorization pour notre JWT
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        
        // ✅ AJOUTER CECI : Headers que le frontend peut lire
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        // ✅ AJOUTER CECI : Permettre les credentials
        configuration.setAllowCredentials(true);
        
        // ✅ AJOUTER CECI : Cache le preflight pendant 1 heure
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}