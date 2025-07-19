package com.document.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {

    private String companyName;
    private String companyKrsNumber;
    private Long companyRegonNumber;
    private Long companyNipNumber;
    private AddressDto companyAddressDto;
}
