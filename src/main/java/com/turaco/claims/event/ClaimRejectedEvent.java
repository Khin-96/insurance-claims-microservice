package com.turaco.claims.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRejectedEvent {
    private Long claimId;
    private String claimNumber;
    private String reason;
    private Instant rejectedAt;
    private String rejectedBy;
}
