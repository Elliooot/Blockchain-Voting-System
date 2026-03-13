DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_name = 'blockchain_event_state'
    ) THEN
        CREATE TABLE blockchain_event_state (
            event_key VARCHAR(128) PRIMARY KEY,
            last_processed_block BIGINT NOT NULL
        );
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uq_vote_ballot_user'
    ) THEN
        ALTER TABLE vote
            ADD CONSTRAINT uq_vote_ballot_user UNIQUE (ballot_id, user_id);
    END IF;
END $$;
