package com.company.service;

import com.company.model.Address;
import com.company.model.dto.AddressDto;

import java.util.UUID;

class AddressSamples {

    final static UUID addressIdI = UUID.fromString("28278ee7-b5ec-4449-aa81-c8a47c60660f");
    final static UUID addressIdII = UUID.fromString("67a86ffd-fa68-42a7-ae70-1f303e821483");
    final static UUID addressIdIII = UUID.fromString("0d50267e-5a05-49ef-8d46-f40f93416f2b");
    final static String addressStreetNameI = "A. Lincoln";
    final static String addressStreetNameII = "M. McArthur";
    final static String addressStreetNameIII = "A. Downey";
    final static String addressStreetNumberI = "16";
    final static String addressStreetNumberII = "77";
    final static String addressStreetNumberIII = "47";
    final static String addressLocalNumberI = "23";
    final static String addressLocalNumberII = "14";
    final static String addressLocalNumberIII = "2";
    final static String addressPostalCodeI = "30-040";
    final static String addressPostalCodeII = "32-708";
    final static String addressPostalCodeIII = "30-123";
    final static String addressCityI = "New York";
    final static String addressCityII = "Paris";
    final static String addressCityIII = "New Orleans";

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
