package com.group_d.paf_server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.group_d.paf_server.security.jwt.AuthTokenFilter;
import com.group_d.paf_server.security.jwt.AuthEntryPointJwt;
import com.group_d.paf_server.service.PlayerDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

/**
 * Konfigurationsklasse für die Sicherheitseinstellungen der Webanwendung.
 * Diese Klasse konfiguriert die Sicherheitsmechanismen wie JWT-Authentifizierung,
 * CORS-Einstellungen und definiert, welche Endpunkte gesichert sind.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    PlayerDetailsService userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    /**
     * Erstellt einen AuthTokenFilter zur Verarbeitung von JWTs für jede Anfrage.
     *
     * @return AuthTokenFilter-Instanz.
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Konfiguriert den AuthenticationProvider mit benutzerdefinierten Benutzerdetails und Passwortencoder.
     *
     * @return Eine Instanz von DaoAuthenticationProvider.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Erstellt und konfiguriert den AuthenticationManager zur Verwaltung der Authentifizierung.
     *
     * @param authConfig Die Konfiguration für die Authentifizierung.
     * @return Eine Instanz von AuthenticationManager.
     * @throws Exception bei Fehlern in der Konfiguration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Erstellt einen Bean für den Passwortverschlüsseler.
     *
     * @return Eine Instanz von PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Konfiguriert die Sicherheitsfilter.
     *
     * @param http Die HttpSecurity-Konfiguration.
     * @return Eine konfigurierte Instanz von SecurityFilterChain.
     * @throws Exception bei Konfigurationsfehlern.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS Konfiguration
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173")); // URL Ihres Frontends
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        http
                .cors(cors -> cors.configurationSource(request -> configuration))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/player/register", "/player/login")
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/player/register", "player/login").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
                //.formLogin(Customizer.withDefaults());

        return http.build();
    }
}