package com.company.service;

import com.company.exception.CompanyMemberAdditionException;
import com.company.exception.CompanyNonExistException;
import com.company.service.implementation.CompanyServiceImplementation;
import com.company.mapper.CompanyMapper;
import com.company.model.dto.CompanyDto;
import com.company.model.dto.UserDto;
import com.company.repository.CompanyRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeParseException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest extends CompanySamples {

    @SuppressWarnings("rawtypes")
    @Mock private RequestHeadersUriSpec requestHeadersUriSpec;
    @SuppressWarnings("rawtypes")
    @Mock private RequestHeadersSpec requestHeadersSpec;
    @Mock private ResponseSpec responseSpec;
    @Mock private WebClient webClient;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private CompanyMapper companyMapper;
    @Mock private CompanyRepository companyRepository;
    @InjectMocks private CompanyServiceImplementation companyService;

    @Test
    @DisplayName("Should return true if company entity with provided krs number exist")
    void test_01() {
        //given
        final var company = sampleEntityCompanyI;

        //when
        when(companyRepository.findCompanyByCompanyKrsNumber(companyKrsNumberI)).thenReturn(Optional.of(company));

        final var result = companyService.checkIfCompanyAlreadyExist(companyKrsNumberI);

        //then
        assertThat(result)
                .isNotNull()
                .isTrue();
        verify(companyRepository).findCompanyByCompanyKrsNumber(companyKrsNumberI);
    }

    @Test
    @DisplayName("Should return false if company entity with provided krs number not exist")
    void test_02() {
        //when
        when(companyRepository.findCompanyByCompanyKrsNumber(companyKrsNumberI)).thenReturn(Optional.empty());

        final var result = companyService.checkIfCompanyAlreadyExist(companyKrsNumberI);

        //then
        assertThat(result)
                .isNotNull()
                .isFalse();
        verify(companyRepository).findCompanyByCompanyKrsNumber(companyKrsNumberI);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should return saved company if valid company dto and founded current user")
    void test_03() {
        //given
        final var sampleToken = "sample-token";
        final var sampleUri = "http://authentication-service/v1.0/authentication/get-id";
        final var companyDto = sampleDtoCompanyI;
        final var savedCompanyDto = sampleDtoCompanyI.toBuilder()
                .companyId(companyIdI)
                .companyMembers(Set.of(currentUserId))
                .build();
        final var companyEntity = sampleEntityCompanyI.toBuilder()
                .companyMembers(Set.of(currentUserId))
                .build();
        final var savedCompanyEntity = companyEntity.toBuilder()
                .companyId(companyIdI)
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
                .hasFieldOrPropertyWithValue("companyId", companyIdI)
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
        final var companyDto = sampleDtoCompanyI;

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

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw an exception if issue appear due mapping malformed company registration date")
    void test_05() {
        //given
        final var sampleToken = "sample-token";
        final var sampleUri = "http://authentication-service/v1.0/authentication/get-id";
        final var malformedCompanyRegistrationDate = "123-error;2137";
        final var companyDto = sampleDtoCompanyI.toBuilder()
                .companyRegistrationDate(malformedCompanyRegistrationDate)
                .build();

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UUID.class)).thenReturn(Mono.just(currentUserId));

        when(companyMapper.mapToEntity(companyDto)).thenThrow(DateTimeParseException.class);

        final var expectedException = catchThrowable(() -> companyService.createCompany(companyDto, sampleToken));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(DateTimeParseException.class);
        verify(companyRepository, never()).save(any());
        verify(companyMapper, never()).mapToDto(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw an exception if issue appear due null company krs number")
    void test_06() {
        //given
        final var sampleToken = "sample-token";
        final var sampleUri = "http://authentication-service/v1.0/authentication/get-id";
        final var companyDto = sampleDtoCompanyI.toBuilder()
                .companyKrsNumber(null)
                .build();
        final var companyEntity = sampleEntityCompanyI.toBuilder()
                .companyKrsNumber(null)
                .build();

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UUID.class)).thenReturn(Mono.just(currentUserId));

        when(companyMapper.mapToEntity(companyDto)).thenReturn(companyEntity);
        when(companyRepository.save(companyEntity)).thenThrow(ConstraintViolationException.class);

        final var expectedException = catchThrowable(() -> companyService.createCompany(companyDto, sampleToken));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(ConstraintViolationException.class);
        verify(companyMapper).mapToEntity(companyDto);
        verify(companyMapper, never()).mapToDto(any());
    }

    @Test
    @DisplayName("Should return founded company dto if company entity exist and company name is valid")
    void test_07() {
        //given
        final var companyDto = sampleDtoCompanyI.toBuilder()
                .companyId(companyIdI)
                .build();
        final var companyEntity = sampleEntityCompanyI.toBuilder()
                .companyId(companyIdI)
                .build();

        //when
        when(companyRepository.findCompanyByCompanyName(companyNameI)).thenReturn(Optional.of(companyEntity));
        when(companyMapper.mapToDto(companyEntity)).thenReturn(companyDto);

        final var result = companyService.getCompanyByName(companyNameI);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(CompanyDto.class)
                .hasFieldOrPropertyWithValue("companyName", companyNameI);
        verify(companyRepository).findCompanyByCompanyName(companyNameI);
        verify(companyMapper).mapToDto(companyEntity);
    }

    @Test
    @DisplayName("Should throw an exception if company entity with provided name not exist")
    void test_08() {
        //given
        final var nonExistingCompanyName = "My fake company sp. z o.o.";

        //when
        when(companyRepository.findCompanyByCompanyName(nonExistingCompanyName)).thenReturn(Optional.empty());

        final var expectedException = catchThrowable(() -> companyService.getCompanyByName(nonExistingCompanyName));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(CompanyNonExistException.class)
                .hasMessageContaining("Couldn't find company using provide company name");
        verify(companyRepository).findCompanyByCompanyName(nonExistingCompanyName);
        verify(companyMapper, never()).mapToDto(any());
    }

    @Test
    @DisplayName("Should return founded company dto if company entity exist and company id is valid")
    void test_09() {
        //given
        final var companyDto = sampleDtoCompanyI.toBuilder()
                .companyId(companyIdI)
                .build();
        final var companyEntity = sampleEntityCompanyI.toBuilder()
                .companyId(companyIdI)
                .build();

        //when
        when(companyRepository.findCompanyByCompanyId(companyIdI)).thenReturn(Optional.of(companyEntity));
        when(companyMapper.mapToDto(companyEntity)).thenReturn(companyDto);

        final var result = companyService.getCompanyByCompanyId(companyIdI.toString());

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(CompanyDto.class)
                .hasFieldOrPropertyWithValue("companyId", companyIdI);
        verify(companyRepository).findCompanyByCompanyId(companyIdI);
        verify(companyMapper).mapToDto(companyEntity);
    }

    @Test
    @DisplayName("Should throw an exception if company entity with provided id not exist")
    void test_10() {
        //given
        final var nonExistingCompanyId = "c74c670b-38b1-4369-8bd2-ec5accaeb3e7";

        //when
        when(companyRepository.findCompanyByCompanyId(UUID.fromString(nonExistingCompanyId))).thenReturn(Optional.empty());

        final var expectedException = catchThrowable(() -> companyService.getCompanyByCompanyId(nonExistingCompanyId));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(CompanyNonExistException.class)
                .hasMessageContaining("Couldn't find company using provide id");
        verify(companyRepository).findCompanyByCompanyId(UUID.fromString(nonExistingCompanyId));
        verify(companyMapper, never()).mapToDto(any());
    }

    @Test
    @DisplayName("Should return founded company dto if company entity exist and company krs number is valid")
    void test_11() {
        //given
        final var companyDto = sampleDtoCompanyI.toBuilder()
                .companyId(companyIdI)
                .build();
        final var companyEntity = sampleEntityCompanyI.toBuilder()
                .companyId(companyIdI)
                .build();

        //when
        when(companyRepository.findCompanyByCompanyKrsNumber(companyKrsNumberI)).thenReturn(Optional.of(companyEntity));
        when(companyMapper.mapToDto(companyEntity)).thenReturn(companyDto);

        final var result = companyService.getCompanyByCompanyKrsNumber(companyKrsNumberI);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(CompanyDto.class)
                .hasFieldOrPropertyWithValue("companyKrsNumber", companyKrsNumberI);
        verify(companyRepository).findCompanyByCompanyKrsNumber(companyKrsNumberI);
        verify(companyMapper).mapToDto(companyEntity);
    }

    @Test
    @DisplayName("Should throw an exception if company entity with provided krs number not exist")
    void test_12() {
        //given
        final var nonExistingKrsNumber = "0000775882";

        //when
        when(companyRepository.findCompanyByCompanyKrsNumber(nonExistingKrsNumber)).thenReturn(Optional.empty());

        final var expectedException = catchThrowable(() -> companyService.getCompanyByCompanyKrsNumber(nonExistingKrsNumber));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(CompanyNonExistException.class)
                .hasMessageContaining("Couldn't find company using provide krs number");
        verify(companyRepository).findCompanyByCompanyKrsNumber(nonExistingKrsNumber);
        verify(companyMapper, never()).mapToDto(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should return list of founded company dtos when company members contain current user id")
    void test_13() {
        //given
        final var sampleToken = "sample-token";
        final var sampleUri = "http://authentication-service/v1.0/authentication/get-id";

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UUID.class)).thenReturn(Mono.just(currentUserId));

        when(companyRepository.findCompaniesByCompanyMembersContaining(currentUserId)).thenReturn(List.of(sampleEntityCompanyII, sampleEntityCompanyIII));
        when(companyMapper.mapToDtos(List.of(sampleEntityCompanyII, sampleEntityCompanyIII))).thenReturn(List.of(sampleDtoCompanyII, sampleDtoCompanyIII));

        final var result = companyService.getCurrentUserCompanies(sampleToken);

        //then
        assertThat(result)
                .isInstanceOf(List.class)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2);
        verify(companyRepository).findCompaniesByCompanyMembersContaining(currentUserId);
        verify(companyMapper).mapToDtos(List.of(sampleEntityCompanyII, sampleEntityCompanyIII));
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should return empty list when any company members not contain current user id")
    void test_14() {
        //given
        final var sampleToken = "sample-token";
        final var sampleUri = "http://authentication-service/v1.0/authentication/get-id";

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UUID.class)).thenReturn(Mono.just(currentUserId));

        when(companyRepository.findCompaniesByCompanyMembersContaining(currentUserId)).thenReturn(Collections.emptyList());
        when(companyMapper.mapToDtos(any())).thenReturn(Collections.emptyList());

        final var result = companyService.getCurrentUserCompanies(sampleToken);

        //then
        assertThat(result)
                .isInstanceOf(List.class)
                .isNotNull()
                .hasSize(0)
                .isEmpty();
        verify(companyRepository).findCompaniesByCompanyMembersContaining(currentUserId);
        verify(companyMapper).mapToDtos(Collections.emptyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw an exception if issue appear due getting current user id")
    void test_15() {
        //given
        final var sampleToken = "sample-token";
        final var sampleUri = "http://authentication-service/v1.0/authentication/get-id";

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UUID.class)).thenThrow(WebClientResponseException.Unauthorized.class);

        final var expectedException = catchThrowable(() -> companyService.getCurrentUserCompanies(sampleToken));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(WebClientResponseException.Unauthorized.class);
        verify(companyRepository, never()).findCompaniesByCompanyMembersContaining(any());
        verify(companyMapper, never()).mapToDtos(any());
    }

    @Test
    @DisplayName("Should add new member id to company members if company entity exist")
    void test_16() {
        //given
        final var companyEntity = sampleEntityCompanyI.toBuilder()
                .companyId(companyIdI)
                .build();
        final var updatedEntity = sampleEntityCompanyI.toBuilder()
                .companyId(companyIdI)
                .companyMembers(Set.of(currentUserId))
                .build();
        //when
        when(companyRepository.findCompanyByCompanyId(companyIdI)).thenReturn(Optional.of(companyEntity));

        companyService.addNewMemberToCompany(companyIdI.toString(), currentUserId.toString());

        //then
        verify(companyRepository).save(updatedEntity);
    }

    @Test
    @DisplayName("Should thrown an exception if company entity not found")
    void test_17() {
        //given
        final var nonExistingCompanyId = "c74c670b-38b1-4369-8bd2-ec5accaeb3e7";

        //when
        when(companyRepository.findCompanyByCompanyId(UUID.fromString(nonExistingCompanyId))).thenReturn(Optional.empty());

        final var expectedException = catchThrowable(() -> companyService.addNewMemberToCompany(nonExistingCompanyId, currentUserId.toString()));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(CompanyMemberAdditionException.class)
                .hasMessageContaining("New member addition failed, company with provided id is not exist");
        verify(companyRepository).findCompanyByCompanyId(UUID.fromString(nonExistingCompanyId));
        verify(companyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return user dto list of company members if company entity was found")
    void test_18() {
        //given
        final var sampleToken = "sample-token";
        final var sampleUri = "http://authentication-service/v1.0/authentication/get-by-id/";
        final var companyEntity = sampleEntityCompanyII.toBuilder()
                .companyId(companyIdII)
                .companyMembers(Set.of(currentUserId, companyMemberId))
                .build();

        //when
        when(companyRepository.findCompanyByCompanyId(companyIdII)).thenReturn(Optional.of(companyEntity));
        var uuidMembersList = companyEntity.getCompanyMembers().stream().toList();
        for (int i = 0; i < uuidMembersList.size(); i++) {
            callWebFluxUserDto(sampleUri + uuidMembersList.get(i).toString(), userDtoList.get(i));
        }

        final var result = companyService.getDetailMembersList(companyIdII.toString(), sampleToken);

        //then
        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .isInstanceOf(List.class);
        verify(companyRepository).findCompanyByCompanyId(companyIdII);
    }

    @Test
    @DisplayName("Should throw an exception if company entity not found using provided company id")
    void test_19() {
        //given
        final var sampleToken = "sample-token";
        final var nonExistingCompanyId = "c74c670b-38b1-4369-8bd2-ec5accaeb3e7";

        //when
        when(companyRepository.findCompanyByCompanyId(UUID.fromString(nonExistingCompanyId))).thenReturn(Optional.empty());

        final var expectedException = catchThrowable(() -> companyService.getDetailMembersList(nonExistingCompanyId, sampleToken));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(CompanyNonExistException.class)
                .hasMessageContaining("Company with provided id is not exist");
        verify(companyRepository).findCompanyByCompanyId(UUID.fromString(nonExistingCompanyId));
        verify(companyRepository, never()).save(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw an exception when some error occur due getting user dto list")
    void test_20() {
        final var wrongToken = "wrong-token";
        final var sampleUri = "http://authentication-service/v1.0/authentication/get-by-id/";
        final var companyEntity = sampleEntityCompanyII.toBuilder()
                .companyId(companyIdII)
                .build();

        //when
        when(companyRepository.findCompanyByCompanyId(companyIdII)).thenReturn(Optional.of(companyEntity));

        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUri + currentUserId)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenThrow(WebClientResponseException.Unauthorized.class);

        final var expectedException = catchThrowable(() -> companyService.getDetailMembersList(companyIdII.toString(), wrongToken));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(WebClientResponseException.Unauthorized.class);
    }

    @SuppressWarnings("unchecked")
    private void callWebFluxUserDto(String url, UserDto userDto) {
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(userDto));
    }
}
