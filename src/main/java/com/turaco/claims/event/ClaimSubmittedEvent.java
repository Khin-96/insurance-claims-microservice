package com.turaco.claims.event;

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
public class ClaimSubmittedEvent {
    private Long claimId;
    private String claimNumber;
    private String policyNumber;
    private Long customerId;
    private String claimType;
    private BigDecimal amount;
    private Instant submittedAt;
    private String submittedBy;
}
