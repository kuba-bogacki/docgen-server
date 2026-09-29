package com.company.controller;

import com.company.exception.AddressNotFoundException;
import com.company.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static com.company.util.ApplicationConstants.API_VERSION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AddressController.class)
class AddressControllerTest extends Samples {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AddressService addressService;

    @Test
    @SneakyThrows
    @DisplayName("When address exist and id is valid then should return address dto")
    void test_01() {
        //when
        doReturn(addressDto).when(addressService).getAddressByAddressId(any(UUID.class));

        mockMvc.perform(get(API_VERSION + "/company/address/{addressId}", addressId)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(addressId))
                .andExpect(jsonPath("$.addressStreetName").value(addressStreetName))
                .andExpect(jsonPath("$.addressStreetNumber").value(addressStreetNumber))
                .andExpect(jsonPath("$.addressLocalNumber").value(addressLocalNumber))
                .andExpect(jsonPath("$.addressPostalCode").value(addressPostalCode))
                .andExpect(jsonPath("$.addressCity").value(addressCity));
    }

    @Test
    @SneakyThrows
    @DisplayName("When address not found then should handle exception")
    void test_02() {
        //when
        doThrow(AddressNotFoundException.class).when(addressService).getAddressByAddressId(any(UUID.class));

        mockMvc.perform(get(API_VERSION + "/company/address/{addressId}", addressId)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When valid address dto provided then should return saved address")
    void test_03() {
        //when
        doReturn(addressDto).when(addressService).createAddress(addressDto);

        mockMvc.perform(post(API_VERSION + "/company/address/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressDto)))
        //then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.addressId").value(addressId))
                .andExpect(jsonPath("$.addressStreetName").value(addressStreetName))
                .andExpect(jsonPath("$.addressStreetNumber").value(addressStreetNumber))
                .andExpect(jsonPath("$.addressLocalNumber").value(addressLocalNumber))
                .andExpect(jsonPath("$.addressPostalCode").value(addressPostalCode))
                .andExpect(jsonPath("$.addressCity").value(addressCity));
    }

    @Test
    @SneakyThrows
    @DisplayName("When dto is valid then should return updated address dto")
    void test_04() {
        //when
        doReturn(addressDto).when(addressService).updateAddress(addressDto);

        mockMvc.perform(put(API_VERSION + "/company/address/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressDto)))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(addressId))
                .andExpect(jsonPath("$.addressStreetName").value(addressStreetName))
                .andExpect(jsonPath("$.addressStreetNumber").value(addressStreetNumber))
                .andExpect(jsonPath("$.addressLocalNumber").value(addressLocalNumber))
                .andExpect(jsonPath("$.addressPostalCode").value(addressPostalCode))
                .andExpect(jsonPath("$.addressCity").value(addressCity));
    }

    @Test
    @SneakyThrows
    @DisplayName("When address not found due updating then should handle exception")
    void test_05() {
        //when
        doThrow(AddressNotFoundException.class).when(addressService).updateAddress(addressDto);

        mockMvc.perform(put(API_VERSION + "/company/address/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressDto)))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return all addresses dto list")
    void test_06() {
        //when
        doReturn(List.of(addressDto)).when(addressService).getAllAddresses();

        mockMvc.perform(get(API_VERSION + "/company/address/get-all")
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].addressId").value(addressId))
                .andExpect(jsonPath("$[0].addressStreetName").value(addressStreetName))
                .andExpect(jsonPath("$[0].addressStreetNumber").value(addressStreetNumber))
                .andExpect(jsonPath("$[0].addressLocalNumber").value(addressLocalNumber))
                .andExpect(jsonPath("$[0].addressPostalCode").value(addressPostalCode))
                .andExpect(jsonPath("$[0].addressCity").value(addressCity));
    }
}