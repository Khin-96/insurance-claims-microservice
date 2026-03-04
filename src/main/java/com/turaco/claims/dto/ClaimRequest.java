package com.turaco.claims.dto;

import com.turaco.claims.domain.ClaimType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {
    
    @NotBlank(message = "Policy number is required")
    @Size(max = 50, message = "Policy number must not exceed 50 characters")
    private String policyNumber;
    
    @NotNull(message = "Claim type is required")
    private ClaimType claimType;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 17, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;
}
