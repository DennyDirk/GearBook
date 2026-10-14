package com.ds.booking_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig
{
    public static final String INVENTORY_COMMANDS = "inventory.commands";

    @Bean
    NewTopic inventoryCommands()
    {
        return TopicBuilder.name(INVENTORY_COMMANDS).partitions(3).replicas(1).build();
    }
}
