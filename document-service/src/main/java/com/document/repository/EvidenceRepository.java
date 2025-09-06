package com.document.repository;

import com.document.model.Evidence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends MongoRepository<Evidence, String> {
    Evidence findByEvidenceId(String evidenceId);
    List<Evidence> findAllByCompanyId(String companyId);
}
