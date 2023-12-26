package com.group_d.paf_server.security.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username;

        // Überprüft, ob das Principal-Objekt eine Instanz von UserDetails ist
        if (principal instanceof UserDetails userDetails) {
            // Wenn ja, holt es den Benutzernamen aus den UserDetails
            username = userDetails.getUsername();
        } else {
            // Wenn nicht, verwendet es die toString-Methode des Principal-Objekts
            // Dies ist für den Fall, dass das Principal-Objekt kein UserDetails ist, sondern ein einfacher String
            username = principal.toString();
        }

        // Erstellt das JWT-Token
        return Jwts.builder()
                .setSubject(username) // Setzt den Benutzernamen als Subject des Tokens
                .setIssuedAt(new Date()) // Setzt das Ausstellungsdatum des Tokens auf das aktuelle Datum
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Setzt das Ablaufdatum des Tokens
                .signWith(SignatureAlgorithm.HS512, jwtSecret) // Signiert das Token mit dem HS512-Algorithmus und dem geheimen Schlüssel
                .compact();
    }


    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
