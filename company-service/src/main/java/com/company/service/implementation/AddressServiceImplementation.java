package com.company.service.implementation;

import com.company.exception.AddressNotFoundException;
import com.company.mapper.AddressMapper;
import com.company.model.dto.AddressDto;
import com.company.repository.AddressRepository;
import com.company.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImplementation implements AddressService {

    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;

    @Override
    @Transactional(readOnly = true)
    public AddressDto getAddressByAddressId(UUID addressId) {
        return addressRepository.findById(addressId)
                .map(addressMapper::mapToDto)
                .orElseThrow(() -> new AddressNotFoundException("Address with provided id does not exist"));
    }

    @Override
    @Transactional
    public AddressDto createAddress(AddressDto addressDto) {
        final var address = addressRepository.save(addressMapper.mapToEntity(addressDto));
        log.info("Address has been created with id : {}", address.getAddressId());
        return addressMapper.mapToDto(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getAllAddresses() {
        return addressMapper.mapToDtos(addressRepository.findAll());
    }

    @Override
    @Transactional
    public AddressDto updateAddress(AddressDto addressDto) {
        addressRepository.findById(addressDto.getAddressId())
                .orElseThrow(() -> new AddressNotFoundException("Address with provided id does not exist"));

        final var updatedAddress = addressRepository.save(addressMapper.mapToEntity(addressDto));
        log.info("Address has been successfully updated with id : {}", updatedAddress.getAddressId());
        return addressMapper.mapToDto(updatedAddress);
    }
}
