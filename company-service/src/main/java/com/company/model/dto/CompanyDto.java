package com.company.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {

    private UUID companyId;
    private String companyName;
    private String companyKrsNumber;
    private Long companyRegonNumber;
    private Long companyNipNumber;
    private String companyRegistrationDate;
    private AddressDto companyAddressDto;
    private Float companyShareCapital;
    private Set<UUID> companyMembers = new HashSet<>();
}
