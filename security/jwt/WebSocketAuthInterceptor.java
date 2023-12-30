package com.group_d.paf_server.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import com.group_d.paf_server.security.jwt.JwtUtils;
import com.group_d.paf_server.service.PlayerDetailsService;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PlayerDetailsService playerDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // Extrahieren des Tokens aus den STOMP-Verbindungseigenschaften
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtils.getUserNameFromJwtToken(token);

            // Laden der UserDetails basierend auf dem Benutzernamen
            UserDetails userDetails = playerDetailsService.loadUserByUsername(username);

            // Erstellen eines Authentication-Objektes
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // Setzen des Authentication-Objektes im SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        return message;
    }
}
