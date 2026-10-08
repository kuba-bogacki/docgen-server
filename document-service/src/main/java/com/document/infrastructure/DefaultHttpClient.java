package com.document.infrastructure;

import com.document.model.dto.CompanyDto;
import com.document.model.dto.EvidenceNotificationDto;
import com.document.model.dto.UserDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static com.document.util.ApplicationConstants.*;
import static com.document.util.HttpClientUtil.buildUrl;
import static com.document.util.HttpClientUtil.setRequestAttributes;

@Component
public class DefaultHttpClient implements HttpClient {

    private final RestClient.Builder restClientBuilder;

    public DefaultHttpClient(@Qualifier("loadBalancedRestClientBuilderDevelopment") RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    @Override
    public UserDto getCurrentUserDto(String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail))
                .build().get()
                .uri(buildUrl(PROTOCOL, AUTHENTICATION_SERVICE, API_VERSION, "/authentication/user"))
                .retrieve()
                .body(UserDto.class);
    }

    @Override
    public CompanyDto getCurrentCompanyDto(String companyId, String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail))
                .build().get()
                .uri(buildUrl(PROTOCOL, COMPANY_SERVICE, API_VERSION, "/company/details/" + companyId))
                .retrieve()
                .body(CompanyDto.class);
    }

    @Override
    public void createCurrentUserNotification(String userEmail, EvidenceNotificationDto evidenceNotificationDto) {
        restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail))
                .build().post()
                .uri(buildUrl(PROTOCOL, NOTIFICATION_SERVICE, API_VERSION, "/notification/create-notification"))
                .body(evidenceNotificationDto)
                .retrieve()
                .toEntity(Void.class);
    }
}
