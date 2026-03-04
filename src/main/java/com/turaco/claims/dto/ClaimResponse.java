package com.turaco.claims.dto;

import com.turaco.claims.domain.Claim;
import com.turaco.claims.domain.ClaimStatus;
import com.turaco.claims.domain.ClaimType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponse {
    private Long id;
    private String claimNumber;
    private String policyNumber;
    private Long customerId;
    private ClaimType claimType;
    private BigDecimal amount;
    private String description;
    private ClaimStatus status;
    private Integer riskScore;
    private Instant submittedAt;
    private Instant processedAt;
    private String createdBy;
    
    public static ClaimResponse from(Claim claim) {
        return ClaimResponse.builder()
            .id(claim.getId())
            .claimNumber(claim.getClaimNumber())
            .policyNumber(claim.getPolicyNumber())
            .customerId(claim.getCustomerId())
            .claimType(claim.getClaimType())
            .amount(claim.getAmount())
            .description(claim.getDescription())
            .status(claim.getStatus())
            .riskScore(claim.getRiskScore())
            .submittedAt(claim.getSubmittedAt())
            .processedAt(claim.getProcessedAt())
            .createdBy(claim.getCreatedBy())
            .build();
    }
}
