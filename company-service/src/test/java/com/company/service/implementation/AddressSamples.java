package com.company.service.implementation;

import com.company.model.Address;
import com.company.model.dto.AddressDto;

import java.util.UUID;

class AddressSamples {

    final UUID addressIdI = UUID.fromString("28278ee7-b5ec-4449-aa81-c8a47c60660f");
    final UUID addressIdII = UUID.fromString("67a86ffd-fa68-42a7-ae70-1f303e821483");
    final UUID addressIdIII = UUID.fromString("0d50267e-5a05-49ef-8d46-f40f93416f2b");
    final String addressStreetNameI = "A. Lincoln";
    final String addressStreetNameII = "M. McArthur";
    final String addressStreetNameIII = "A. Downey";
    final String addressStreetNumberI = "16";
    final String addressStreetNumberII = "77";
    final String addressStreetNumberIII = "47";
    final String addressLocalNumberI = "23";
    final String addressLocalNumberII = "14";
    final String addressLocalNumberIII = "2";
    final String addressPostalCodeI = "30-040";
    final String addressPostalCodeII = "32-708";
    final String addressPostalCodeIII = "30-123";
    final String addressCityI = "New York";
    final String addressCityII = "Paris";
    final String addressCityIII = "New Orleans";

    AddressDto sampleDtoAddressI = AddressDto.builder()
            .addressStreetName(addressStreetNameI)
            .addressStreetNumber(addressStreetNumberI)
            .addressLocalNumber(addressLocalNumberI)
            .addressPostalCode(addressPostalCodeI)
            .addressCity(addressCityI)
            .build();

    Address sampleEntityAddressI = Address.builder()
            .addressStreetName(addressStreetNameI)
            .addressStreetNumber(addressStreetNumberI)
            .addressLocalNumber(addressLocalNumberI)
            .addressPostalCode(addressPostalCodeI)
            .addressCity(addressCityI)
            .build();

    AddressDto sampleDtoAddressII = AddressDto.builder()
            .addressId(addressIdII)
            .addressStreetName(addressStreetNameII)
            .addressStreetNumber(addressStreetNumberII)
            .addressLocalNumber(addressLocalNumberII)
            .addressPostalCode(addressPostalCodeII)
            .addressCity(addressCityII)
            .build();

    Address sampleEntityAddressII = Address.builder()
            .addressId(addressIdII)
            .addressStreetName(addressStreetNameII)
            .addressStreetNumber(addressStreetNumberII)
            .addressLocalNumber(addressLocalNumberII)
            .addressPostalCode(addressPostalCodeII)
            .addressCity(addressCityII)
            .build();

    AddressDto sampleDtoAddressIII = AddressDto.builder()
            .addressId(addressIdIII)
            .addressStreetName(addressStreetNameIII)
            .addressStreetNumber(addressStreetNumberIII)
            .addressLocalNumber(addressLocalNumberIII)
            .addressPostalCode(addressPostalCodeIII)
            .addressCity(addressCityIII)
            .build();

    Address sampleEntityAddressIII = Address.builder()
            .addressId(addressIdIII)
            .addressStreetName(addressStreetNameIII)
            .addressStreetNumber(addressStreetNumberIII)
            .addressLocalNumber(addressLocalNumberIII)
            .addressPostalCode(addressPostalCodeIII)
            .addressCity(addressCityIII)
            .build();
}
