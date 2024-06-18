package com.company.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID companyId;
    private String companyName;
    private String companyKrsNumber;
    private Long companyRegonNumber;
    private Long companyNipNumber;
    private LocalDate companyRegistrationDate;
    @OneToOne(cascade = CascadeType.ALL)
    private Address companyAddress;
    private Float companyShareCapital;
//    @CollectionTable(joinColumns = @JoinColumn(name = "company_id", nullable = false))
    @ElementCollection
    private List<UUID> companyMembers = new ArrayList<>();
}