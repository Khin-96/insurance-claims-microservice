package com.turaco.claims.service;

import com.turaco.claims.domain.Claim;
import com.turaco.claims.domain.ClaimType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class RiskScoringService {
    
    /**
     * Calculate risk score based on claim amount and type
     * Score range: 0-100 (higher = riskier)
     */
    public int calculateRiskScore(Claim claim) {
        int score = 0;
        
        // Amount-based scoring
        BigDecimal amount = claim.getAmount();
        if (amount.compareTo(BigDecimal.valueOf(10000)) < 0) {
            score += 10;
        } else if (amount.compareTo(BigDecimal.valueOf(50000)) < 0) {
            score += 30;
        } else if (amount.compareTo(BigDecimal.valueOf(100000)) < 0) {
            score += 50;
        } else {
            score += 70;
        }
        
        // Type-based scoring
        score += switch (claim.getClaimType()) {
            case MEDICAL -> 20;
            case ACCIDENT -> 30;
            case LIFE -> 40;
            case PROPERTY -> 25;
            case AUTO -> 20;
            case TRAVEL -> 15;
            case OTHER -> 35;
        };
        
        // Cap at 100
        score = Math.min(score, 100);
        
        log.info("Calculated risk score {} for claim {}", score, claim.getClaimNumber());
        return score;
    }
    
    public boolean isHighRisk(int riskScore) {
        return riskScore >= 70;
    }
}
