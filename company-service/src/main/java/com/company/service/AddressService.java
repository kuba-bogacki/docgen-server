package com.company.service;

import com.company.model.dto.AddressDto;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    AddressDto getAddressByAddressId(UUID addressId);
    AddressDto createAddress(AddressDto addressDto);
    List<AddressDto> getAllAddresses();
    AddressDto updateAddress(AddressDto addressDto);
}
