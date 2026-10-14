package com.ds.booking_service.outbox;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID>
{
    Optional<OutboxEvent> findByAggregateId(UUID aggregateId);

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status, Limit limit);
}
