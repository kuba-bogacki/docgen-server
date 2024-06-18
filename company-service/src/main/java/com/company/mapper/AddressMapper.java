package com.company.mapper;

import com.company.model.Address;
import com.company.model.dto.AddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address mapToEntity(AddressDto addressDto);
    AddressDto mapToDto(Address address);
    List<Address> mapToEntities(List<AddressDto> addressDtos);
    List<AddressDto> mapToDtos(List<Address> addresses);
}
