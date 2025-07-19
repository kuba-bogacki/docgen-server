package com.document.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    private String addressStreetName;
    private String addressStreetNumber;
    private String addressLocalNumber;
    private String addressPostalCode;
    private String addressCity;
}
