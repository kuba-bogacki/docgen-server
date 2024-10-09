package com.company.service;

import com.company.mapper.CompanyMapper;
import com.company.model.dto.CompanyDto;
import com.company.repository.CompanyRepository;
import com.company.service.implementation.CompanyServiceImplementation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest extends CompanySamples {

    @Mock private CompanyRepository companyRepository;
    @Mock private CompanyMapper companyMapper;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @SuppressWarnings("rawtypes")
    @Mock private RequestHeadersUriSpec requestHeadersUriSpec;
    @SuppressWarnings("rawtypes")
    @Mock private RequestHeadersSpec requestHeadersSpec;
    @Mock private ResponseSpec responseSpec;
    @InjectMocks private CompanyServiceImplementation companyService;

    @Test
    @DisplayName("Should return true if company entity with provided krs number exist")
    void test_01() {
        //given
        final var company = sampleEntityCompany;

        //when
        when(companyRepository.findCompanyByCompanyKrsNumber(companyKrsNumber)).thenReturn(Optional.of(company));

        final var result = companyService.checkIfCompanyAlreadyExist(companyKrsNumber);

        //then
        assertThat(result)
                .isNotNull()
                .isTrue();
        verify(companyRepository).findCompanyByCompanyKrsNumber(companyKrsNumber);
    }

    @Test
    @DisplayName("Should return false if company entity with provided krs number not exist")
    void test_02() {
        //when
        when(companyRepository.findCompanyByCompanyKrsNumber(companyKrsNumber)).thenReturn(Optional.empty());

        final var result = companyService.checkIfCompanyAlreadyExist(companyKrsNumber);

        //then
        assertThat(result)
                .isNotNull()
                .isFalse();
        verify(companyRepository).findCompanyByCompanyKrsNumber(companyKrsNumber);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should return saved company if valid company dto and founded current user")
    void test_03() {
        //given
        final var sampleToken = "sample-token";
        final var sampleUri = "http://authentication-service/v1.0/authentication/get-id";
        final var companyDto = sampleDtoCompany;
        final var savedCompanyDto = sampleDtoCompany.toBuilder()
                .companyId(companyId)
                .companyMembers(Set.of(currentUserId))
                .build();
        final var companyEntity = sampleEntityCompany.toBuilder()
                .companyMembers(Set.of(currentUserId))
                .build();
        final var savedCompanyEntity = companyEntity.toBuilder()
                .companyId(companyId)
                .build();

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UUID.class)).thenReturn(Mono.just(currentUserId));

        when(companyMapper.mapToEntity(companyDto)).thenReturn(companyEntity);
        when(companyRepository.save(companyEntity)).thenReturn(savedCompanyEntity);
        when(companyMapper.mapToDto(savedCompanyEntity)).thenReturn(savedCompanyDto);

        final var result = companyService.createCompany(companyDto, sampleToken);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(CompanyDto.class)
                .hasFieldOrPropertyWithValue("companyId", companyId)
                .hasFieldOrPropertyWithValue("companyMembers", Set.of(currentUserId));
        verify(companyRepository).save(companyEntity);
        verify(companyMapper).mapToDto(savedCompanyEntity);
        verify(companyMapper).mapToEntity(companyDto);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw an exception if issue appear due getting user id")
    void test_04() {
        //given
        final var sampleToken = "sample-token";
        final var sampleUri = "http://authentication-service/v1.0/authentication/get-id";
        final var companyDto = sampleDtoCompany;

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UUID.class)).thenThrow(WebClientResponseException.Unauthorized.class);

        final var expectedException = catchThrowable(() -> companyService.createCompany(companyDto, sampleToken));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(WebClientResponseException.Unauthorized.class);
        verify(companyRepository, never()).save(any());
        verify(companyMapper, never()).mapToDto(any());
        verify(companyMapper, never()).mapToEntity(any());
    }
}