
CREATE TABLE IF NOT EXISTS grievance_event (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    grievance_id   UUID NOT NULL REFERENCES grievance(id) ON DELETE CASCADE,
    citizen_id     UUID NOT NULL REFERENCES citizen(id),
    event_type     VARCHAR(80) NOT NULL,
    -- e.g. GRIEVANCE_SUBMITTED, GRIEVANCE_IN_PROGRESS, GRIEVANCE_RESOLVED_WARD
    -- GRIEVANCE_INVALID_TRANSITION is also written on bad attempts (audit trail)
    old_status     VARCHAR(30),
    new_status     VARCHAR(30),
    actor_id       UUID REFERENCES users(id),
    actor_role     VARCHAR(30),
    note           TEXT,      -- resolution text, rejection reason, escalation reason
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_grev_event_grievance ON grievance_event(grievance_id);
CREATE INDEX IF NOT EXISTS idx_grev_event_citizen   ON grievance_event(citizen_id);
CREATE INDEX IF NOT EXISTS idx_grev_event_type      ON grievance_event(event_type);
