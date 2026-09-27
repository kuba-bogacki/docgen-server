package com.company.controller;

import com.company.model.dto.AddressDto;

import java.util.UUID;

public class Samples {

    final UUID addressId = UUID.fromString("cdf356e0-8cc2-41db-a6bd-0697ec002418");
    final String addressStreetName = "Nixon";
    final String addressStreetNumber = "14";
    final String addressLocalNumber = "55";
    final String addressPostalCode = "30-567";
    final String addressCity = "Milan";

    final AddressDto addressDto = AddressDto.builder()
            .addressId(addressId)
            .addressStreetName(addressStreetName)
            .addressStreetNumber(addressStreetNumber)
            .addressLocalNumber(addressLocalNumber)
            .addressPostalCode(addressPostalCode)
            .addressCity(addressCity)
            .build();
}
