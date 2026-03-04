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
public class ClaimApprovedEvent {
    private Long claimId;
    private String claimNumber;
    private BigDecimal amount;
    private Instant approvedAt;
    private String approvedBy;
}
