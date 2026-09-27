package com.company.controller;

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

import java.util.UUID;

import static com.company.util.ApplicationConstants.API_VERSION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        mockMvc.perform(get(API_VERSION + "/company/address/{addressId}", String.valueOf(addressId))
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(String.valueOf(addressId)))
                .andExpect(jsonPath("$.addressStreetName").value(addressStreetName))
                .andExpect(jsonPath("$.addressStreetNumber").value(addressStreetNumber))
                .andExpect(jsonPath("$.addressLocalNumber").value(addressLocalNumber))
                .andExpect(jsonPath("$.addressPostalCode").value(addressPostalCode))
                .andExpect(jsonPath("$.addressCity").value(addressCity));
    }
}