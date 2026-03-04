package com.turaco.claims.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    
    @Bean
    public NewTopic claimSubmittedTopic() {
        return TopicBuilder.name("claim-submitted")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic claimApprovedTopic() {
        return TopicBuilder.name("claim-approved")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic claimRejectedTopic() {
        return TopicBuilder.name("claim-rejected")
            .partitions(3)
            .replicas(1)
            .build();
    }
}
