package com.authentication.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotBlank(message = "User email can't be blank")
    @Email(message = "User email should be valid")
    private String userEmail;

    @NotBlank(message = "User password can't be blank")
    private String userPassword;
}
