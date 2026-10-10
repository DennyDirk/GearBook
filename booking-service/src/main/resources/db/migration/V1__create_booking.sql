CREATE TABLE booking (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    inventory_id BIGINT NOT NUll,
    quantity INT NOT NULL CHECK ( quantity > 0 ),
    status varchar(16) NOT NULL CHECK ( status in ('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED' ) ),
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
)