package com.ds.booking_service.outbox;

import com.ds.booking_service.config.KafkaTopicConfig;
import org.springframework.data.domain.Limit;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class OutboxPublisher
{

    private final OutboxRepository outboxRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(OutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate)
    {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.delay-ms}")
    public void publishBatch()
    {
        List<OutboxEvent> outboxEvents = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.NEW,
                Limit.of(100));

        for (OutboxEvent event : outboxEvents)
        {
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(KafkaTopicConfig.INVENTORY_COMMANDS, event.getAggregateId().toString(),
                            event.getPayload());

            try
            {
                future.get();
                event.markPublished();
                outboxRepository.save(event);

            } catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Kafka publishing interrupted", e);
            } catch (ExecutionException e)
            {
                throw new RuntimeException("Failed to publish outbox event", e);
            }
        }

    }

}
