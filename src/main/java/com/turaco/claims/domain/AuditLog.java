package com.turaco.claims.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "audit_log", indexes = {
    @Index(name = "idx_audit_claim_id", columnList = "claim_id"),
    @Index(name = "idx_audit_performed_at", columnList = "performed_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "claim_id")
    private Long claimId;
    
    @Column(nullable = false, length = 50)
    private String action;
    
    @Column(name = "performed_by", nullable = false)
    private String performedBy;
    
    @Column(name = "performed_at", nullable = false)
    private Instant performedAt;
    
    @Column(name = "old_status", length = 20)
    private String oldStatus;
    
    @Column(name = "new_status", length = 20)
    private String newStatus;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> details;
    
    @PrePersist
    protected void onCreate() {
        performedAt = Instant.now();
    }
}
