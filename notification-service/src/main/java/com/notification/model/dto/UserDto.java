package com.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String userId;
    private String userFirstNameI;
    private String userLastNameI;
    private String userEmail;
    private String userVerificationCode;
    private String userPrincipal;
}
