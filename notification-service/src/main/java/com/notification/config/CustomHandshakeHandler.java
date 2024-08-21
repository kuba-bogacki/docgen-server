package com.notification.config;

import com.sun.security.auth.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import reactor.util.annotation.NonNull;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(@NonNull ServerHttpRequest request, @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        final var customHandshakeHandlerUuid = UUID.randomUUID().toString();
        return new UserPrincipal(customHandshakeHandlerUuid);
    }
}
