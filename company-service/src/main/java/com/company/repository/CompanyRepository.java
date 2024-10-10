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
    Optional<Company> findCompanyByCompanyKrsNumber(String companyKrsNumber);
    Optional<Company> findCompanyByCompanyName(String companyName);
    Optional<Company> findCompanyByCompanyId(UUID companyId);
    List<Company> findCompaniesByCompanyMembersContaining(UUID memberId);
}
