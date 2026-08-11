
CREATE TABLE IF NOT EXISTS grievance (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    citizen_id              UUID NOT NULL REFERENCES citizen(id) ON DELETE CASCADE,
    filed_by                UUID REFERENCES users(id),

    category                VARCHAR(50) NOT NULL CHECK (category IN (
                                 'DATA_INACCURACY','BENEFIT_DENIAL','ID_CARD_ISSUE',
                                 'PRIVACY_VIOLATION','SYSTEM_ACCESS','OTHER')),
    description             TEXT NOT NULL,
    attachment_urls         TEXT[],

    -- Format: GRV-2026-000123 — unique, citizen-facing, generated server-side
    tracking_code           VARCHAR(20) NOT NULL UNIQUE,

    filed_at                TIMESTAMPTZ NOT NULL DEFAULT now(),

    status                  VARCHAR(30) NOT NULL DEFAULT 'RECEIVED' CHECK (status IN (
                                 'RECEIVED','IN_PROGRESS','RESOLVED_WARD','REFERRED_JUDICIAL',
                                 'RESOLVED_JUDICIAL','REFERRED_BOARD','RESOLVED_BOARD',
                                 'REFERRED_COMMISSION','CLOSED','REOPENED','CLOSED_INVALID')),

    escalated_at            TIMESTAMPTZ,
    escalated_by            UUID REFERENCES users(id),

    resolution_ward         TEXT,
    resolution_ward_at      TIMESTAMPTZ,
    resolution_ward_by      UUID REFERENCES users(id),

    resolution_judicial     TEXT,
    resolution_judicial_at  TIMESTAMPTZ,

    resolution_board        TEXT,
    resolution_board_at     TIMESTAMPTZ,

    rejection_reason        TEXT,

    closed_at               TIMESTAMPTZ,
    closed_by               UUID REFERENCES users(id),
    reopen_count            SMALLINT NOT NULL DEFAULT 0,

    -- 48-hour SLA from filed_at — set at insert time by the service layer
    sla_due_at              TIMESTAMPTZ,
    sla_breached            BOOLEAN NOT NULL DEFAULT FALSE,

    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_grievance_citizen   ON grievance(citizen_id);
CREATE INDEX IF NOT EXISTS idx_grievance_status     ON grievance(status);
CREATE INDEX IF NOT EXISTS idx_grievance_tracking   ON grievance(tracking_code);
CREATE INDEX IF NOT EXISTS idx_grievance_sla        ON grievance(sla_due_at)
    WHERE status NOT IN ('CLOSED','CLOSED_INVALID');