package com.company.controller;

import com.company.model.dto.AddressDto;
import com.company.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.company.util.ApplicationConstants.API_VERSION;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/company")
public class AddressController {

    private final AddressService addressService;

    @GetMapping(value = "/address/{addressId}")
    public ResponseEntity<AddressDto> getAddressByAddressId(@PathVariable String addressId) {
        return new ResponseEntity<>(addressService.getAddressByAddressId(UUID.fromString(addressId)), HttpStatus.OK);
    }

    @PostMapping(value = "/address/create")
    public ResponseEntity<AddressDto> createAddress(@RequestBody AddressDto addressDto) {
        return new ResponseEntity<>(addressService.createAddress(addressDto), HttpStatus.CREATED);
    }

    @PutMapping(value = "/address/update")
    public ResponseEntity<AddressDto> updateAddress(@RequestBody AddressDto addressDto) {
        return new ResponseEntity<>(addressService.updateAddress(addressDto), HttpStatus.OK);
    }

    @GetMapping(value = "/address/get-all")
    public ResponseEntity<List<AddressDto>> getAllAddresses() {
        return new ResponseEntity<>(addressService.getAllAddresses(), HttpStatus.OK);
    }
}
