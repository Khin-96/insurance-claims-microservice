package com.turaco.claims.controller;

import com.turaco.claims.domain.ClaimStatus;
import com.turaco.claims.dto.ClaimRequest;
import com.turaco.claims.dto.ClaimResponse;
import com.turaco.claims.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
@Tag(name = "Claims", description = "Claims management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ClaimController {
    
    private final ClaimService claimService;
    
    @PostMapping
    @Operation(summary = "Submit a new claim")
    public ResponseEntity<ClaimResponse> submitClaim(@Valid @RequestBody ClaimRequest request) {
        ClaimResponse response = claimService.submitClaim(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get claim by ID")
    public ResponseEntity<ClaimResponse> getClaimById(@PathVariable Long id) {
        ClaimResponse response = claimService.getClaimById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Get all claims (Admin/Agent only)")
    public ResponseEntity<List<ClaimResponse>> getAllClaims(
            @RequestParam(required = false) ClaimStatus status) {
        List<ClaimResponse> claims = status != null 
            ? claimService.getClaimsByStatus(status)
            : claimService.getAllClaims();
        return ResponseEntity.ok(claims);
    }
    
    @GetMapping("/my-claims")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get my claims (Customer only)")
    public ResponseEntity<List<ClaimResponse>> getMyClaimsAsCustomer() {
        List<ClaimResponse> claims = claimService.getMyClaimsAsCustomer();
        return ResponseEntity.ok(claims);
    }
    
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve a claim (Admin only)")
    public ResponseEntity<ClaimResponse> approveClaim(@PathVariable Long id) {
        ClaimResponse response = claimService.approveClaim(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject a claim (Admin only)")
    public ResponseEntity<ClaimResponse> rejectClaim(
            @PathVariable Long id,
            @RequestParam String reason) {
        ClaimResponse response = claimService.rejectClaim(id, reason);
        return ResponseEntity.ok(response);
    }
}
