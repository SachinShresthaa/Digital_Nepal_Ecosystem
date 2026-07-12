-- V5__eligibility_rules_schema.sql
-- Placeholder: tables for eligibility rules and program enrollment.
-- Example actions:
--   - CREATE TABLE programs (...)
--   - CREATE TABLE eligibility_rules (...)

/* Migration SQL goes here */
-- TABLE: disability_profile (WHO ICF standard)
-- One-to-one with citizen. Severity scale: 0=none, 1=mild, 2=moderate,
-- 3=severe, 4=complete
--
-- ELIGIBILITY RULE: Disability ID card requires:
-- MAX(severity_body, severity_activity, severity_participation) >= 2
-- AND certificate_no IS NOT NULL
-- ============================================================================
CREATE TABLE IF NOT EXISTS disability_profile (
    id                      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    citizen_id              UUID        NOT NULL UNIQUE REFERENCES citizen(id),

    -- PHYSICAL/SENSORY_VISION/SENSORY_HEARING/INTELLECTUAL/PSYCHOSOCIAL/SPEECH/MULTIPLE
    disability_type         VARCHAR(50) NOT NULL,

    -- WHO ICF severity dimensions: 0-4
    severity_body           SMALLINT    NOT NULL CHECK (severity_body BETWEEN 0 AND 4),
    severity_activity       SMALLINT    NOT NULL CHECK (severity_activity BETWEEN 0 AND 4),
    severity_participation  SMALLINT    NOT NULL CHECK (severity_participation BETWEEN 0 AND 4),

    -- Certificate fields — certificate_no must be present for ID card eligibility
    certificate_no          VARCHAR(100),
    issuing_hospital        VARCHAR(300),
    certificate_date        DATE,
    certificate_expiry      DATE,

    assistive_device        VARCHAR(100),
    device_provided         BOOLEAN     DEFAULT FALSE,
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_disability_citizen ON disability_profile(citizen_id);

-- ============================================================================
-- TABLE: employment_profile
-- One-to-one with citizen. Stores employment category + JSONB sub-fields.
--
-- ELIGIBILITY RULE: Unemployment ID card requires:
-- category = 'UNEMPLOYED' AND citizen age >= 18
-- ============================================================================
CREATE TABLE IF NOT EXISTS employment_profile (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    citizen_id  UUID        NOT NULL UNIQUE REFERENCES citizen(id),

    -- UNEMPLOYED/SELF_EMPLOYED/FARMER/GOVERNMENT/PRIVATE/
    -- FOREIGN/STUDENT/HOMEMAKER/RETIRED/DISABLED_UNABLE
    category    VARCHAR(30) NOT NULL,

    -- Category-specific sub-fields as JSONB
    sub_fields  JSONB       NOT NULL DEFAULT '{}',

    income_band VARCHAR(30),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by  UUID        NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_employment_citizen ON employment_profile(citizen_id);
CREATE INDEX idx_employment_category ON employment_profile(category);
