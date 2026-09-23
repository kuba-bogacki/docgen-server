package com.notification.configuration;

import com.sun.security.auth.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import reactor.util.annotation.NonNull;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import static com.notification.util.ApplicationConstants.ALLOWED_ORIGIN_PATTERN;
import static com.notification.util.ApplicationConstants.WEBSOCKET_ENDPOINT;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint(WEBSOCKET_ENDPOINT)
                .setAllowedOriginPatterns(ALLOWED_ORIGIN_PATTERN)
                .setHandshakeHandler(getCustomHandshakeHandler())
                .withSockJS()
                .setSuppressCors(true);
    }

    @Bean
    public DefaultHandshakeHandler getCustomHandshakeHandler() {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(@NonNull ServerHttpRequest request, @NonNull WebSocketHandler wsHandler,
                                              @NonNull Map<String, Object> attributes) {
                final var customHandshakeHandlerUuid = UUID.randomUUID().toString();
                return new UserPrincipal(customHandshakeHandlerUuid);
            }
        };
    }
}
