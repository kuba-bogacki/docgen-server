package com.authentication.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String userFirstNameI;
    private String userFirstNameII;
    private String userLastNameI;
    private String userLastNameII;
    private String userEmail;
    private String userPassword;
    private String userGender;
    private Boolean termsAndCondition;
}
