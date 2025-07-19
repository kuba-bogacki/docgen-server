package com.document.model.dto;

import com.document.util.annotation.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialStatementDto {

    @NotBlank(message = "Company id can't be null or blank")
    private String companyId;

    @Date
    private String periodStartDate;

    @Date
    private String periodEndDate;

    @NotNull(message = "Company equity can't be null")
    private Float companyEquity;

    @NotNull(message = "Company total sum can't be null")
    private Float companyTotalSum;

    @NotNull(message = "Company net profit can't be null")
    private Float companyNetProfit;

    @NotNull(message = "Company net increase can't be null")
    private Float companyNetIncrease;

    @NotBlank(message = "Management board president can't be null or blank")
    private String managementBoardPresident;

    @NotBlank(message = "Supervisory board president can't be null or blank")
    private String supervisoryBoardChairman;

    private List<String> supervisoryBoardMembers;
}
