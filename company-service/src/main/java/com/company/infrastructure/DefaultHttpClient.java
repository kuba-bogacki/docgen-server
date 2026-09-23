package com.company.infrastructure;

import com.company.model.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static com.company.util.ApplicationConstants.*;
import static com.company.util.HttpClientUtil.*;

@Component
@RequiredArgsConstructor
public class DefaultHttpClient implements HttpClient {

    private final RestClient.Builder restClientBuilder;

    @Override
    public UUID getCurrentUserId(String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail))
                .build().get()
                .uri(buildUrl(PROTOCOL, AUTHENTICATION_SERVICE, API_VERSION, "/authentication/get-id"))
                .retrieve()
                .body(UUID.class);
    }

    @Override
    public UserDto getAuthenticationServiceUserDto(String userId, String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail))
                .build().get()
                .uri(buildUrl(PROTOCOL, AUTHENTICATION_SERVICE, API_VERSION, "/authentication/get-by-id/" + userId))
                .retrieve()
                .body(UserDto.class);
    }
}
