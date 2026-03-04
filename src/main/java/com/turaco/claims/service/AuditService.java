package com.turaco.claims.service;

import com.turaco.claims.domain.AuditLog;
import com.turaco.claims.domain.Claim;
import com.turaco.claims.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    
    private final AuditLogRepository auditLogRepository;
    
    @Transactional
    public void logClaimSubmission(Claim claim, String username) {
        Map<String, Object> details = new HashMap<>();
        details.put("policyNumber", claim.getPolicyNumber());
        details.put("amount", claim.getAmount());
        details.put("claimType", claim.getClaimType().name());
        
        AuditLog log = AuditLog.builder()
            .claimId(claim.getId())
            .action("CLAIM_SUBMITTED")
            .performedBy(username)
            .newStatus(claim.getStatus().name())
            .details(details)
            .build();
        
        auditLogRepository.save(log);
        log.info("Logged claim submission: {}", claim.getClaimNumber());
    }
    
    @Transactional
    public void logClaimApproval(Claim claim, String username) {
        AuditLog log = AuditLog.builder()
            .claimId(claim.getId())
            .action("CLAIM_APPROVED")
            .performedBy(username)
            .oldStatus("PENDING")
            .newStatus("APPROVED")
            .build();
        
        auditLogRepository.save(log);
        log.info("Logged claim approval: {}", claim.getClaimNumber());
    }
    
    @Transactional
    public void logClaimRejection(Claim claim, String username, String reason) {
        Map<String, Object> details = new HashMap<>();
        details.put("reason", reason);
        
        AuditLog log = AuditLog.builder()
            .claimId(claim.getId())
            .action("CLAIM_REJECTED")
            .performedBy(username)
            .oldStatus("PENDING")
            .newStatus("REJECTED")
            .details(details)
            .build();
        
        auditLogRepository.save(log);
        log.info("Logged claim rejection: {}", claim.getClaimNumber());
    }
    
    public List<AuditLog> getClaimAuditTrail(Long claimId) {
        return auditLogRepository.findByClaimIdOrderByPerformedAtDesc(claimId);
    }
}
