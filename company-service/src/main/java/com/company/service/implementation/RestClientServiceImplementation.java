package com.company.service.implementation;

import com.company.model.dto.UserDto;
import com.company.service.RestClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static com.company.util.ApplicationConstants.*;
import static com.company.util.UrlBuilder.addTokenHeader;
import static com.company.util.UrlBuilder.buildUrl;

@Component
@RequiredArgsConstructor
public class RestClientServiceImplementation implements RestClientService {

    private final RestClient.Builder restClientBuilder;

    @Override
    public UUID getCurrentUserId(String jwtToken) {
        return restClientBuilder
                .requestInterceptor(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, AUTHENTICATION_SERVICE, API_VERSION, "/authentication/get-id"))
                .retrieve()
                .body(UUID.class);
    }

    @Override
    public UserDto getAuthenticationServiceUserDto(String userId, String jwtToken) {
        return restClientBuilder
                .requestInterceptor(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, AUTHENTICATION_SERVICE, API_VERSION, "/authentication/get-by-id/" + userId))
                .retrieve()
                .body(UserDto.class);
    }
}
