package com.company.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID companyId;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String companyKrsNumber;

    @Column(nullable = false)
    private Long companyRegonNumber;

    @Column(nullable = false)
    private Long companyNipNumber;

    private LocalDate companyRegistrationDate;

    @JoinColumn(nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private Address companyAddress;

    private Float companyShareCapital;

    @ElementCollection
    private Set<UUID> companyMembers = new HashSet<>();
}