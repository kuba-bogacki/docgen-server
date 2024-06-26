package com.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {

    private UUID companyId;
    private String companyName;
    private String companyKrsNumber;
    private List<UUID> companyMembers = new ArrayList<>();
}
