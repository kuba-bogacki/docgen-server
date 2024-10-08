package com.company.service;

import com.company.mapper.CompanyMapper;
import com.company.model.Company;
import com.company.repository.CompanyRepository;
import com.company.service.implementation.CompanyServiceImplementation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest extends CompanySamples {

    @Mock private CompanyRepository companyRepository;
    @Mock private CompanyMapper companyMapper;
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
}