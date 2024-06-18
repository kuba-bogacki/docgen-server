package com.company.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    private UUID addressId;
    private String addressStreetName;
    private String addressStreetNumber;
    private String addressLocalNumber;
    private String addressPostalCode;
    private String addressCity;
}
