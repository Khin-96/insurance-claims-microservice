package com.turaco.claims.repository;

import com.turaco.claims.domain.Claim;
import com.turaco.claims.domain.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    Optional<Claim> findByClaimNumber(String claimNumber);
    List<Claim> findByCustomerId(Long customerId);
    List<Claim> findByStatus(ClaimStatus status);
    List<Claim> findByPolicyNumber(String policyNumber);
    
    @Query("SELECT c FROM Claim c WHERE c.customerId = :customerId AND c.status = :status")
    List<Claim> findByCustomerIdAndStatus(Long customerId, ClaimStatus status);
}
