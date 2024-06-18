package com.company.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String userFirstNameI;
    private String userFirstNameII;
    private String userLastNameI;
    private String userLastNameII;
    private String userEmail;
    private String userPhotoFileName;
}
