package com.authentication.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.authentication.util.ApplicationConstants.EMAIL_PATTERN;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "User first name can't be blank")
    private String userFirstNameI;

    private String userFirstNameII;

    @NotBlank(message = "User last name can't be blank")
    private String userLastNameI;

    private String userLastNameII;

    @NotBlank(message = "User email can't be blank")
    @Email(message = "User email should be valid", regexp = EMAIL_PATTERN)
    private String userEmail;

    private String userPassword;

    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "User gender should be MALE, FEMALE or OTHER")
    private String userGender;

    private Boolean termsAndCondition;
}
