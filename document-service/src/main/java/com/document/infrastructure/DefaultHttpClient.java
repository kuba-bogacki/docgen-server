package com.document.infrastructure;

import com.document.model.dto.CompanyDto;
import com.document.model.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static com.document.util.ApplicationConstants.API_VERSION;
import static com.document.util.ApplicationConstants.PROTOCOL;
import static com.document.util.HttpClientUtil.buildUrl;
import static com.document.util.HttpClientUtil.setRequestAttributes;

@Component
@RequiredArgsConstructor
public class DefaultHttpClient implements HttpClient {

    private final RestClient.Builder restClientBuilder;

    @Override
    public UserDto getCurrentUserDto(String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail))
                .build().get()
                .uri(buildUrl(PROTOCOL, "authentication-service", API_VERSION, "/authentication/user"))
                .retrieve()
                .body(UserDto.class);
    }

    @Override
    public CompanyDto getCurrentCompanyDto(String companyId, String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail))
                .build().get()
                .uri(buildUrl(PROTOCOL, "company-service", API_VERSION, "/company/details/" + companyId))
                .retrieve()
                .body(CompanyDto.class);
    }
}
