package com.group_d.paf_server.config;

import com.group_d.paf_server.security.jwt.WebSocketAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Diese Klasse aktiviert und konfiguriert den WebSocket-Message-Broker,
 * der für die Echtzeitkommunikation zwischen Client und Server verwendet wird.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }

    /**
     * Registriert STOMP-Endpunkte, die von WebSockets genutzt werden.
     * STOMP = Simple Text Oriented Messaging Protocol, wird
     * hier für die Nachrichtenübermittlung über WebSockets verwendet.
     *
     * @param registry Das Registry-Objekt für die Konfiguration der Endpunkte.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registriert den WebSocket-Endpunkt, der von Clients genutzt wird, um eine Verbindung herzustellen.
        // SockJS wird verwendet, um WebSocket-Kommunikation auch in Browsern zu ermöglichen, die kein WebSocket unterstützen.
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000", "http://localhost:5173")
                .withSockJS();
    }

    /**
     * Konfiguriert den MessageBroker, der Nachrichten an Clients weiterleitet.
     *
     * @param registry Das Registry-Objekt für die Konfiguration des MessageBrokers.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Setzt Präfixe für Ziele, an die Nachrichten von Clients gesendet werden.
        registry.setApplicationDestinationPrefixes("/app");
        // Aktiviert einen MessageBroker, der Nachrichten an Client-Subscriptions weiterleitet.
        registry.enableSimpleBroker("/topic");
    }
}
