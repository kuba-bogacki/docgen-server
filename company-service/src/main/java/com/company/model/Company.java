package com.company.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company other)) return false;
        return this.companyId != null && this.companyId.equals(other.getCompanyId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}