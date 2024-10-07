package com.company.service;

import com.company.exception.AddressNotFoundException;
import com.company.mapper.AddressMapper;
import com.company.model.dto.AddressDto;
import com.company.repository.AddressRepository;
import com.company.service.implementation.AddressServiceImplementation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest extends AddressSamples {

    @Mock private AddressRepository addressRepository;
    @Mock private AddressMapper addressMapper;
    @InjectMocks private AddressServiceImplementation addressService;

    @Test
    @DisplayName("Should return address dto if entity with provided id exist")
    void test_01() {
        //given
        var expectedDto = sampleDtoAddressI.toBuilder()
                .addressId(addressIdI)
                .build();
        var foundedEntity = sampleEntityAddressI.toBuilder()
                .addressId(addressIdI)
                .build();

        //when
        when(addressRepository.findAddressByAddressId(addressIdI)).thenReturn(Optional.of(foundedEntity));
        when(addressMapper.mapToDto(foundedEntity)).thenReturn(expectedDto);

        final var result = addressService.getAddressByAddressId(addressIdI);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(AddressDto.class)
                .isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("Should throw an exception if entity with provided id not found")
    void test_02() {
        //given

        //when
        when(addressRepository.findAddressByAddressId(any(UUID.class))).thenReturn(Optional.empty());

        final var expectedException = catchException(() -> addressService.getAddressByAddressId(addressIdI));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(AddressNotFoundException.class)
                .hasMessageContaining("Address with provided id does not exist");
        verify(addressMapper, never()).mapToDto(any());
    }

    @Test
    @DisplayName("Should create address and return address dto if address data is valid")
    void test_03() {
        //given
        var inputDto = sampleDtoAddressI;
        var inputEntity = sampleEntityAddressI;
        var savedEntity = sampleEntityAddressI.toBuilder()
                .addressId(addressIdI)
                .build();
        var expectedDto = sampleDtoAddressI.toBuilder()
                .addressId(addressIdI)
                .build();

        //when
        when(addressMapper.mapToEntity(inputDto)).thenReturn(inputEntity);
        when(addressMapper.mapToDto(savedEntity)).thenReturn(expectedDto);
        when(addressRepository.save(inputEntity)).thenReturn(savedEntity);

        final var result = addressService.createAddress(inputDto);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(AddressDto.class)
                .isEqualTo(expectedDto);
        verify(addressMapper).mapToEntity(inputDto);
        verify(addressRepository).save(inputEntity);
        verify(addressMapper).mapToDto(savedEntity);
    }

    @Test
    @DisplayName("Should return all addresses dtos list if any entities exists")
    void test_04() {
        //given
        var sampleDto = sampleDtoAddressI.toBuilder()
                .addressId(addressIdI)
                .build();
        var sampleEntity = sampleEntityAddressI.toBuilder()
                .addressId(addressIdI)
                .build();

        //when
        when(addressMapper.mapToDtos(List.of(sampleEntity, sampleEntityAddressII, sampleEntityAddressIII)))
                .thenReturn(List.of(sampleDto, sampleDtoAddressII, sampleDtoAddressIII));
        when(addressRepository.findAll()).thenReturn(List.of(sampleEntity, sampleEntityAddressII, sampleEntityAddressIII));

        final var result = addressService.getAllAddresses();

        //then
        assertThat(result)
                .isNotEmpty()
                .isInstanceOf(List.class)
                .hasSize(3)
                .isNotNull();
        verify(addressMapper).mapToDtos(List.of(sampleEntity, sampleEntityAddressII, sampleEntityAddressIII));
        verify(addressRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty addresses dtos list if no entities exist")
    void test_05() {
        //when
        when(addressMapper.mapToDtos(anyList())).thenReturn(Collections.emptyList());
        when(addressRepository.findAll()).thenReturn(Collections.emptyList());

        final var result = addressService.getAllAddresses();

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0)
                .isEmpty();
        verify(addressMapper).mapToDtos(Collections.emptyList());
        verify(addressRepository).findAll();
    }

    @Test
    @DisplayName("Should return dto of updated entity if entity already exist")
    void test_06() {
        //given
        var providedDto = sampleDtoAddressII.toBuilder()
                .addressId(addressIdI)
                .build();
        var foundedEntity = sampleEntityAddressI.toBuilder()
                .addressId(addressIdI)
                .build();
        var savedEntity = sampleEntityAddressII.toBuilder()
                .addressId(addressIdI)
                .build();
        var expectedDto = sampleDtoAddressII.toBuilder()
                .addressId(addressIdI)
                .build();

        //when
        when(addressRepository.findAddressByAddressId(addressIdI)).thenReturn(Optional.of(foundedEntity));
        when(addressMapper.mapToEntity(providedDto)).thenReturn(savedEntity);
        when(addressRepository.save(savedEntity)).thenReturn(savedEntity);
        when(addressMapper.mapToDto(savedEntity)).thenReturn(expectedDto);

        final var result = addressService.updateAddress(providedDto);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(AddressDto.class)
                .isEqualTo(expectedDto);
        verify(addressMapper).mapToEntity(providedDto);
        verify(addressMapper).mapToDto(savedEntity);
        verify(addressRepository).findAddressByAddressId(addressIdI);
    }
}