CREATE TABLE outbox (
    id uuid primary key,
    aggregate_id uuid not null,
    event_type varchar(32) not null,
    payload jsonb not null,
    status varchar(16) not null CHECK (status IN ('NEW', 'PUBLISHED')),
    created_at timestamptz not null default CURRENT_TIMESTAMP,
    published_at timestamptz,
    CONSTRAINT fk_outbox_booking FOREIGN KEY (aggregate_id) REFERENCES booking(id)
);
CREATE INDEX idx_outbox_status_new_created_at on outbox(created_at) where status='NEW';