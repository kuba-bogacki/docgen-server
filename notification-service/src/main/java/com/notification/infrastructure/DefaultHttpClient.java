package com.notification.infrastructure;

import com.notification.model.dto.CompanyDto;
import com.notification.model.dto.UserDto;
import com.notification.model.dto.UserPrincipalDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static com.notification.util.ApplicationConstants.*;
import static com.notification.util.HttpClientUtil.buildUrl;
import static com.notification.util.HttpClientUtil.setRequestAttributes;

@Component
@RequiredArgsConstructor
public class DefaultHttpClient implements HttpClient {

    private final RestClient.Builder restClientBuilder;

    @Override
    public UserDto getUserDtoById(String userId, String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail)).build()
                .get()
                .uri(buildUrl(PROTOCOL, "authentication-service", API_VERSION, "/authentication/get-by-id/" + userId))
                .retrieve()
                .body(UserDto.class);
    }

    @Override
    public UserDto getCurrentUserDto(String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail)).build()
                .get()
                .uri(buildUrl(PROTOCOL, "authentication-service", API_VERSION, "/authentication/user"))
                .retrieve()
                .body(UserDto.class);
    }

    @Override
    public CompanyDto getCompanyDtoById(String companyId, String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail)).build()
                .get()
                .uri(buildUrl(PROTOCOL, "company-service", API_VERSION, "/company/details/" + companyId))
                .retrieve()
                .body(CompanyDto.class);
    }

    @Override
    public ResponseEntity<?> addUserPrincipalName(UserPrincipalDto userPrincipalDto, String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail)).build()
                .put()
                .uri(buildUrl(PROTOCOL, "authentication-service", API_VERSION, "/authentication/add-user-principal"))
                .body(userPrincipalDto)
                .retrieve()
                .toEntity(ResponseEntity.class);
    }

    @Override
    public ResponseEntity<?> addNewCompanyMember(String companyId, String userId, String userEmail) {
        return restClientBuilder
                .requestInterceptor(setRequestAttributes(userEmail)).build()
                .put()
                .uri(buildUrl(PROTOCOL, "company-service", API_VERSION, "/company/add-new-member/" + companyId))
                .body(userId)
                .retrieve()
                .toEntity(ResponseEntity.class);
    }
}
