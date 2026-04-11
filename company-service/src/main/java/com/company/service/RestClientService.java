package com.company.service;

import com.company.model.dto.UserDto;

import java.util.UUID;

public interface RestClientService {
    UUID getCurrentUserId(String jwtToken);
    UserDto getAuthenticationServiceUserDto(String userId, String jwtToken);
}
