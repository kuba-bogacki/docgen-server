package com.authentication.model.dto;

import com.authentication.model.type.Membership;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MembershipDto {

    @NotNull(message = "Membership can't be null")
    private Membership membership;
}
