package com.turaco.claims.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "claims", indexes = {
    @Index(name = "idx_claim_number", columnList = "claim_number"),
    @Index(name = "idx_policy_number", columnList = "policy_number"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_customer_id", columnList = "customer_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Claim {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "claim_number", unique = true, nullable = false, length = 50)
    private String claimNumber;
    
    @Column(name = "policy_number", nullable = false, length = 50)
    private String policyNumber;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "claim_type", nullable = false, length = 50)
    private ClaimType claimType;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClaimStatus status;
    
    @Column(name = "risk_score")
    private Integer riskScore;
    
    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;
    
    @Column(name = "processed_at")
    private Instant processedAt;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @PrePersist
    protected void onCreate() {
        submittedAt = Instant.now();
        updatedAt = Instant.now();
        if (status == null) {
            status = ClaimStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
