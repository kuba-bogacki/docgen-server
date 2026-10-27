package com.company.repository;

import com.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    @Query("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.companyMembers WHERE c.companyKrsNumber = :companyKrsNumber")
    Optional<Company> findCompanyByCompanyKrsNumber(String companyKrsNumber);

    @Query("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.companyMembers WHERE c.companyName = :companyName")
    Optional<Company> findCompanyByCompanyName(String companyName);

    @Query("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.companyMembers WHERE c.companyId = :companyId")
    Optional<Company> findCompanyByCompanyId(UUID companyId);

    @Query("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.companyMembers WHERE :memberId MEMBER OF c.companyMembers")
    List<Company> findCompaniesByCompanyMembersContaining(UUID memberId);
}
