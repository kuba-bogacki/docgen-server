package com.company.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
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
}