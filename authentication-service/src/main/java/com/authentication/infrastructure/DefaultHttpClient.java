package com.authentication.infrastructure;

import com.authentication.model.User;
import com.authentication.model.dto.UserEventDto;
import com.authentication.security.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

import static com.authentication.util.ApplicationConstants.API_VERSION;
import static com.authentication.util.ApplicationConstants.PROTOCOL;
import static com.authentication.util.HttpClientUtil.buildUrl;
import static com.authentication.util.HttpClientUtil.setRequestAttributes;

@Component
@RequiredArgsConstructor
public class DefaultHttpClient implements HttpClient {

    private final RestClient.Builder restClientBuilder;

    @Override
    public ResponseEntity<?> getEmailStatus(UserEventDto userEventDto) {
        return restClientBuilder.build().post()
                .uri(buildUrl(PROTOCOL, "notification-service", API_VERSION, "/notification/verification"))
                .body(userEventDto)
                .retrieve()
                .toEntity(ResponseEntity.class);
    }

    @Override
    public ResponseEntity<?> getJoiningStatus(User user, String companyId, String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail))
                .build().put()
                .uri(buildUrl(PROTOCOL, "company-service", API_VERSION, "/company/add-new-member/" + companyId))
                .body(user.getUserId().toString())
                .retrieve()
                .toEntity(ResponseEntity.class);
    }

    @Override
    public ResponseEntity<?> sendRefreshToken(String userPrincipal, AuthenticationResponse authenticationResponse, String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail))
                .build().post()
                .uri(buildUrl(PROTOCOL, "notification-service", API_VERSION, "/notification/refresh-token/" + userPrincipal))
                .body(authenticationResponse)
                .retrieve()
                .toEntity(ResponseEntity.class);
    }

    @Override
    public List<UUID> getMemberUuidList(String companyId, String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail))
                .build().get()
                .uri(buildUrl(PROTOCOL, "company-service", API_VERSION, "/company/company-members/" + companyId.substring(0, companyId.length() - 1)))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
