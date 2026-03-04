package com.turaco.claims.service;

import com.turaco.claims.domain.Claim;
import com.turaco.claims.event.ClaimApprovedEvent;
import com.turaco.claims.event.ClaimRejectedEvent;
import com.turaco.claims.event.ClaimSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimEventPublisher {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String CLAIM_SUBMITTED_TOPIC = "claim-submitted";
    private static final String CLAIM_APPROVED_TOPIC = "claim-approved";
    private static final String CLAIM_REJECTED_TOPIC = "claim-rejected";
    
    public void publishClaimSubmitted(Claim claim, String username) {
        ClaimSubmittedEvent event = ClaimSubmittedEvent.builder()
            .claimId(claim.getId())
            .claimNumber(claim.getClaimNumber())
            .policyNumber(claim.getPolicyNumber())
            .customerId(claim.getCustomerId())
            .claimType(claim.getClaimType().name())
            .amount(claim.getAmount())
            .submittedAt(claim.getSubmittedAt())
            .submittedBy(username)
            .build();
        
        kafkaTemplate.send(CLAIM_SUBMITTED_TOPIC, claim.getClaimNumber(), event);
        log.info("Published ClaimSubmittedEvent for claim: {}", claim.getClaimNumber());
    }
    
    public void publishClaimApproved(Claim claim, String username) {
        ClaimApprovedEvent event = ClaimApprovedEvent.builder()
            .claimId(claim.getId())
            .claimNumber(claim.getClaimNumber())
            .amount(claim.getAmount())
            .approvedAt(Instant.now())
            .approvedBy(username)
            .build();
        
        kafkaTemplate.send(CLAIM_APPROVED_TOPIC, claim.getClaimNumber(), event);
        log.info("Published ClaimApprovedEvent for claim: {}", claim.getClaimNumber());
    }
    
    public void publishClaimRejected(Claim claim, String username, String reason) {
        ClaimRejectedEvent event = ClaimRejectedEvent.builder()
            .claimId(claim.getId())
            .claimNumber(claim.getClaimNumber())
            .reason(reason)
            .rejectedAt(Instant.now())
            .rejectedBy(username)
            .build();
        
        kafkaTemplate.send(CLAIM_REJECTED_TOPIC, claim.getClaimNumber(), event);
        log.info("Published ClaimRejectedEvent for claim: {}", claim.getClaimNumber());
    }
}
