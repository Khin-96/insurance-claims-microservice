package com.turaco.claims.service;

import com.turaco.claims.domain.Claim;
import com.turaco.claims.domain.ClaimStatus;
import com.turaco.claims.dto.ClaimRequest;
import com.turaco.claims.dto.ClaimResponse;
import com.turaco.claims.exception.ClaimNotFoundException;
import com.turaco.claims.exception.DuplicateClaimException;
import com.turaco.claims.exception.UnauthorizedException;
import com.turaco.claims.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimService {
    
    private final ClaimRepository claimRepository;
    private final IdempotencyService idempotencyService;
    private final RiskScoringService riskScoringService;
    private final AuditService auditService;
    private final ClaimEventPublisher eventPublisher;
    
    @Transactional
    public ClaimResponse submitClaim(ClaimRequest request) {
        String username = getCurrentUsername();
        
        // Check for duplicate submission
        if (idempotencyService.isDuplicate(request.getIdempotencyKey())) {
            log.warn("Duplicate claim submission detected for key: {}", request.getIdempotencyKey());
            return idempotencyService.getCachedResponse(request.getIdempotencyKey())
                .orElseThrow(() -> new DuplicateClaimException("Duplicate claim submission"));
        }
        
        // Create claim
        Claim claim = Claim.builder()
            .claimNumber(generateClaimNumber())
            .policyNumber(request.getPolicyNumber())
            .customerId(getCurrentUserId())
            .claimType(request.getClaimType())
            .amount(request.getAmount())
            .description(request.getDescription())
            .status(ClaimStatus.PENDING)
            .createdBy(username)
            .build();
        
        // Calculate risk score
        int riskScore = riskScoringService.calculateRiskScore(claim);
        claim.setRiskScore(riskScore);
        
        // Auto-approve low-risk claims
        if (riskScore < 30) {
            claim.setStatus(ClaimStatus.APPROVED);
            claim.setProcessedAt(Instant.now());
        }
        
        // Save claim
        Claim savedClaim = claimRepository.save(claim);
        
        // Audit log
        auditService.logClaimSubmission(savedClaim, username);
        
        // Publish event
        eventPublisher.publishClaimSubmitted(savedClaim, username);
        
        // Cache response
        ClaimResponse response = ClaimResponse.from(savedClaim);
        idempotencyService.storeResponse(request.getIdempotencyKey(), response);
        
        log.info("Claim submitted successfully: {}", savedClaim.getClaimNumber());
        return response;
    }
    
    @Transactional
    public ClaimResponse approveClaim(Long claimId) {
        String username = getCurrentUsername();
        
        Claim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new ClaimNotFoundException("Claim not found: " + claimId));
        
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new IllegalStateException("Only pending claims can be approved");
        }
        
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setProcessedAt(Instant.now());
        
        Claim savedClaim = claimRepository.save(claim);
        
        // Audit log
        auditService.logClaimApproval(savedClaim, username);
        
        // Publish event
        eventPublisher.publishClaimApproved(savedClaim, username);
        
        log.info("Claim approved: {}", savedClaim.getClaimNumber());
        return ClaimResponse.from(savedClaim);
    }
    
    @Transactional
    public ClaimResponse rejectClaim(Long claimId, String reason) {
        String username = getCurrentUsername();
        
        Claim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new ClaimNotFoundException("Claim not found: " + claimId));
        
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new IllegalStateException("Only pending claims can be rejected");
        }
        
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setProcessedAt(Instant.now());
        
        Claim savedClaim = claimRepository.save(claim);
        
        // Audit log
        auditService.logClaimRejection(savedClaim, username, reason);
        
        // Publish event
        eventPublisher.publishClaimRejected(savedClaim, username, reason);
        
        log.info("Claim rejected: {}", savedClaim.getClaimNumber());
        return ClaimResponse.from(savedClaim);
    }
    
    public ClaimResponse getClaimById(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new ClaimNotFoundException("Claim not found: " + claimId));
        
        // Check authorization
        if (!canAccessClaim(claim)) {
            throw new UnauthorizedException("Not authorized to access this claim");
        }
        
        return ClaimResponse.from(claim);
    }
    
    public List<ClaimResponse> getAllClaims() {
        return claimRepository.findAll().stream()
            .map(ClaimResponse::from)
            .collect(Collectors.toList());
    }
    
    public List<ClaimResponse> getClaimsByStatus(ClaimStatus status) {
        return claimRepository.findByStatus(status).stream()
            .map(ClaimResponse::from)
            .collect(Collectors.toList());
    }
    
    public List<ClaimResponse> getMyClaimsAsCustomer() {
        Long customerId = getCurrentUserId();
        return claimRepository.findByCustomerId(customerId).stream()
            .map(ClaimResponse::from)
            .collect(Collectors.toList());
    }
    
    private String generateClaimNumber() {
        return "CLM-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
    
    private Long getCurrentUserId() {
        // In real implementation, extract from JWT or UserDetails
        return 1L; // Placeholder
    }
    
    private boolean canAccessClaim(Claim claim) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        
        // ADMIN and AGENT can access all claims
        if (auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_AGENT"))) {
            return true;
        }
        
        // CUSTOMER can only access their own claims
        return claim.getCustomerId().equals(getCurrentUserId());
    }
}
