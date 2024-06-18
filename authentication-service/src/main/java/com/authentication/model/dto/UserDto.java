package com.authentication.model.dto;

import com.authentication.model.type.Gender;
import com.authentication.model.type.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private UUID userId;
    private String userFirstNameI;
    private String userFirstNameII;
    private String userLastNameI;
    private String userLastNameII;
    private String userEmail;
    private Gender userGender;
    private Role userRole;
    private String userVerificationCode;
    private String userPhotoFileName;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean enabled;
}
