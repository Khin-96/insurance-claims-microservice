package com.turaco.claims.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turaco.claims.dto.ClaimResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {
    
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String KEY_PREFIX = "idempotency:";
    private static final Duration TTL = Duration.ofHours(24);
    
    public boolean isDuplicate(String idempotencyKey) {
        String key = KEY_PREFIX + idempotencyKey;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    public void storeResponse(String idempotencyKey, ClaimResponse response) {
        try {
            String key = KEY_PREFIX + idempotencyKey;
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(key, json, TTL);
            log.info("Stored idempotency response for key: {}", idempotencyKey);
        } catch (Exception e) {
            log.error("Failed to store idempotency response", e);
        }
    }
    
    public Optional<ClaimResponse> getCachedResponse(String idempotencyKey) {
        try {
            String key = KEY_PREFIX + idempotencyKey;
            String json = redisTemplate.opsForValue().get(key);
            if (json != null) {
                ClaimResponse response = objectMapper.readValue(json, ClaimResponse.class);
                log.info("Retrieved cached response for key: {}", idempotencyKey);
                return Optional.of(response);
            }
        } catch (Exception e) {
            log.error("Failed to retrieve cached response", e);
        }
        return Optional.empty();
    }
}
