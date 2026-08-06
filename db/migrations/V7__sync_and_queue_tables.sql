-- V7__sync_and_queue_tables.sql
-- Day 7 - Sync Foundation

-- ==========================================================
-- SYNC BATCH
-- ==========================================================
CREATE TABLE sync_batch (

                            batch_id UUID PRIMARY KEY,

                            ward_id UUID NOT NULL,
                            submitted_by UUID NOT NULL,

                            device_id VARCHAR(200) NOT NULL,

                            record_count INTEGER NOT NULL DEFAULT 0,
                            conflict_count INTEGER NOT NULL DEFAULT 0,

                            status VARCHAR(20) NOT NULL DEFAULT 'PROCESSING',

                            submitted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            completed_at TIMESTAMPTZ,

                            error_message TEXT,

                            CONSTRAINT fk_sync_batch_ward
                                FOREIGN KEY (ward_id)
                                    REFERENCES ward(id),

                            CONSTRAINT fk_sync_batch_user
                                FOREIGN KEY (submitted_by)
                                    REFERENCES users(id)
);

-- ==========================================================
-- SYNC CONFLICT REGISTRY
-- ==========================================================
CREATE TABLE sync_conflict_registry (

                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                        citizen_id UUID NOT NULL,

                                        submitting_user_id UUID,
                                        device_id VARCHAR(200),

                                        server_version INTEGER NOT NULL,
                                        device_version INTEGER NOT NULL,

                                        conflicting_data JSONB NOT NULL,

                                        resolution_status VARCHAR(30)
                                            NOT NULL DEFAULT 'PENDING_REVIEW',

                                        resolved_by UUID,
                                        resolved_at TIMESTAMPTZ,

                                        CONSTRAINT fk_sync_conflict_citizen
                                            FOREIGN KEY (citizen_id)
                                                REFERENCES citizen(id),

                                        CONSTRAINT fk_sync_conflict_user
                                            FOREIGN KEY (submitting_user_id)
                                                REFERENCES users(id),

                                        CONSTRAINT fk_sync_conflict_resolved_by
                                            FOREIGN KEY (resolved_by)
                                                REFERENCES users(id)
);

-- ==========================================================
-- INDEXES
-- ==========================================================
CREATE INDEX idx_sync_batch_device
    ON sync_batch(device_id);

CREATE INDEX idx_sync_batch_status
    ON sync_batch(status);

CREATE INDEX idx_sync_conflict_citizen
    ON sync_conflict_registry(citizen_id);

CREATE INDEX idx_sync_conflict_status
    ON sync_conflict_registry(resolution_status);