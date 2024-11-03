package com.authentication.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipalDto {

    @NotBlank(message = "User id can't be blank")
    private String userId;

    @NotBlank(message = "User principal can't be blank")
    private String userPrincipal;
}
