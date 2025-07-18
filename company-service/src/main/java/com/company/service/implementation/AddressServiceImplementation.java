package com.company.service.implementation;

import com.company.exception.AddressNotFoundException;
import com.company.mapper.AddressMapper;
import com.company.model.Address;
import com.company.model.dto.AddressDto;
import com.company.repository.AddressRepository;
import com.company.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImplementation implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public AddressDto getAddressByAddressId(UUID addressId) {
        Optional<Address> address = addressRepository.findAddressByAddressId(addressId);

        if (address.isEmpty()) {
            throw new AddressNotFoundException("Address with provided id does not exist");
        }
        return addressMapper.mapToDto(address.get());
    }

    @Override
    public AddressDto createAddress(AddressDto addressDto) {
        Address address = addressRepository.save(addressMapper.mapToEntity(addressDto));
        log.debug("Address has been created with id : {}", address.getAddressId());
        return addressMapper.mapToDto(address);
    }

    @Override
    public List<AddressDto> getAllAddresses() {
        return addressMapper.mapToDtos(addressRepository.findAll());
    }

    @Override
    public AddressDto updateAddress(AddressDto addressDto) {
        Optional<Address> address = addressRepository.findAddressByAddressId(addressDto.getAddressId());

        if (address.isEmpty()) {
            throw new AddressNotFoundException("Address with provided id does not exist");
        }
        Address updatedAddress = addressRepository.save(addressMapper.mapToEntity(addressDto));
        return addressMapper.mapToDto(updatedAddress);
    }
}
