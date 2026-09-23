package com.company.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID addressId;

    @Column(nullable = false)
    private String addressStreetName;

    @Column(nullable = false)
    private String addressStreetNumber;

    private String addressLocalNumber;

    @Column(nullable = false)
    private String addressPostalCode;

    @Column(nullable = false)
    private String addressCity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address other)) return false;
        return this.addressId != null && this.addressId.equals(other.getAddressId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}