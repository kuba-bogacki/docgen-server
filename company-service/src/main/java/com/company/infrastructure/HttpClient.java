package com.company.infrastructure;

import com.company.model.dto.UserDto;

import java.util.UUID;

public interface HttpClient {
    UUID getCurrentUserId(String userEmail);
    UserDto getAuthenticationServiceUserDto(String userId, String userEmail);
}
