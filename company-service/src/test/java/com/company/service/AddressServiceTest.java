package com.company.service;

import com.company.exception.AddressNotFoundException;
import com.company.mapper.AddressMapper;
import com.company.model.dto.AddressDto;
import com.company.repository.AddressRepository;
import com.company.service.implementation.AddressServiceImplementation;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    @DisplayName("Should throw an exception if address dto is null")
    void test_04() {
        //given
        AddressDto nullAddressDto = null;

        //when
        when(addressMapper.mapToEntity(nullAddressDto)).thenReturn(null);
        when(addressRepository.save(any())).thenThrow(IllegalArgumentException.class);

        final var expectedException = catchException(() -> addressService.createAddress(nullAddressDto));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class);
        verify(addressMapper, never()).mapToDto(any());
    }

    @Test
    @DisplayName("Should throw an exception if address dto have some null fields")
    void test_05() {
        //given
        final var invalidAddressDto = sampleDtoAddressI.toBuilder()
                .addressStreetName(null)
                .addressCity(null)
                .build();
        final var invalidAddressEntity = sampleEntityAddressI.toBuilder()
                .addressStreetName(null)
                .addressCity(null)
                .build();

        //when
        when(addressMapper.mapToEntity(invalidAddressDto)).thenReturn(invalidAddressEntity);
        when(addressRepository.save(invalidAddressEntity)).thenThrow(ConstraintViolationException.class);

        final var expectedException = catchException(() -> addressService.createAddress(invalidAddressDto));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(ConstraintViolationException.class);
        verify(addressMapper, never()).mapToDto(any());
    }

    @Test
    @DisplayName("Should return all addresses dtos list if any entities exists")
    void test_06() {
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
    void test_07() {
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
    void test_08() {
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

    @Test
    @DisplayName("Should throw an exception if address entity to update was not found")
    void test_09() {
        //when
        when(addressRepository.findAddressByAddressId(any(UUID.class))).thenReturn(Optional.empty());

        final var expectedException = catchException(() -> addressService.getAddressByAddressId(addressIdII));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(AddressNotFoundException.class)
                .hasMessageContaining("Address with provided id does not exist");
        verify(addressMapper, never()).mapToEntity(any());
        verify(addressRepository, never()).save(any());
        verify(addressMapper, never()).mapToDto(any());
    }
}