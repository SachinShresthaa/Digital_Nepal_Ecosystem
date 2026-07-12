-- =============================================================================
-- 1. Geographic reference tables (ward hierarchy)
--    Needed by RLS subqueries. Coordinate with Amit — if V1 already has
--    these tables, drop the CREATE TABLE blocks below and keep only the seed.
-- =============================================================================

CREATE TABLE IF NOT EXISTS auth.provinces (
    id   UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(200) NOT NULL,
    code SMALLINT    NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS auth.municipalities (
    id          UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    province_id UUID        NOT NULL REFERENCES auth.provinces(id),
    name        VARCHAR(200) NOT NULL,
    code        VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS auth.wards (
    id              UUID     PRIMARY KEY DEFAULT uuid_generate_v4(),
    municipality_id UUID     NOT NULL REFERENCES auth.municipalities(id),
    ward_no         SMALLINT NOT NULL,
    name            VARCHAR(200),
    UNIQUE (municipality_id, ward_no)
);

-- ward_id column on citizens (add only if Amit has not added it in V1)
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE  table_schema = 'citizen_registry'
          AND  table_name   = 'citizens'
          AND  column_name  = 'ward_id'
    ) THEN
        ALTER TABLE citizen_registry.citizens
            ADD COLUMN ward_id UUID REFERENCES auth.wards(id);
    END IF;
END $$;

-- =============================================================================
-- 2. Seed Kummayak data (safe to re-run — ON CONFLICT DO NOTHING)
-- =============================================================================

INSERT INTO auth.provinces (id, name, code)
VALUES ('00000000-0000-0000-0000-000000000001', 'Koshi Province', 1)
ON CONFLICT DO NOTHING;

INSERT INTO auth.municipalities (id, province_id, name, code)
VALUES (
    '00000000-0000-0000-0000-000000000010',
    '00000000-0000-0000-0000-000000000001',
    'Kummayak Rural Municipality',
    'KUMMAYAK-001'
) ON CONFLICT DO NOTHING;

-- 9 wards for Kummayak
INSERT INTO auth.wards (municipality_id, ward_no, name)
SELECT '00000000-0000-0000-0000-000000000010', s.n, 'Ward ' || s.n
FROM   generate_series(1, 9) AS s(n)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 3. Application roles — one per government tier
-- =============================================================================

DO $$ BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'ward_admin_role') THEN
        CREATE ROLE ward_admin_role;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'local_body_role') THEN
        CREATE ROLE local_body_role;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'province_role') THEN
        CREATE ROLE province_role;
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'central_role') THEN
        CREATE ROLE central_role;
    END IF;
END $$;

-- =============================================================================
-- 4. Schema access
-- =============================================================================

GRANT USAGE ON SCHEMA citizen_registry
    TO ward_admin_role, local_body_role, province_role, central_role;

GRANT USAGE ON SCHEMA auth
    TO ward_admin_role, local_body_role, province_role, central_role;

GRANT USAGE ON SCHEMA employment
    TO ward_admin_role, local_body_role, province_role, central_role;

-- =============================================================================
-- 5. Table-level grants per tier
--    Ward + Local Body : full write
--    Province + Central: SELECT only — enforced at BOTH grant AND policy level
-- =============================================================================

GRANT SELECT, INSERT, UPDATE ON citizen_registry.citizens
    TO ward_admin_role, local_body_role;

GRANT SELECT ON citizen_registry.citizens
    TO province_role, central_role;

GRANT SELECT, INSERT, UPDATE ON employment.employment_records
    TO ward_admin_role, local_body_role;

GRANT SELECT ON employment.employment_records
    TO province_role, central_role;

-- =============================================================================
-- 6. Enable RLS on citizens table
--    FORCE = even the table owner (app DB user) is filtered
-- =============================================================================

ALTER TABLE citizen_registry.citizens ENABLE  ROW LEVEL SECURITY;
ALTER TABLE citizen_registry.citizens FORCE   ROW LEVEL SECURITY;

-- =============================================================================
-- 7. Drop old policies safely before recreating
-- =============================================================================

DROP POLICY IF EXISTS ward_citizen_policy       ON citizen_registry.citizens;
DROP POLICY IF EXISTS local_body_citizen_policy ON citizen_registry.citizens;
DROP POLICY IF EXISTS province_citizen_policy   ON citizen_registry.citizens;
DROP POLICY IF EXISTS central_citizen_policy    ON citizen_registry.citizens;

-- =============================================================================
-- 8. THE 4 RLS POLICIES
--
--    Session variable        Set by Spring from JWT claim
--    app.current_ward_id          → ward_id  claim in JWT
--    app.current_municipality_id  → municipality_id claim
--    app.current_province_id      → province_id claim
--
--    current_setting('var', true) — the "true" means:
--      return NULL (not ERROR) if the variable is not set.
--      Without it, unauthenticated requests throw an exception.
-- =============================================================================

-- Policy 1 — Ward admin: own ward citizens only
CREATE POLICY ward_citizen_policy
    ON citizen_registry.citizens
    AS PERMISSIVE
    FOR ALL
    TO ward_admin_role
    USING (
        ward_id = current_setting('app.current_ward_id', true)::UUID
    );

-- Policy 2 — Local body admin: all wards inside their municipality
CREATE POLICY local_body_citizen_policy
    ON citizen_registry.citizens
    AS PERMISSIVE
    FOR ALL
    TO local_body_role
    USING (
        ward_id IN (
            SELECT id FROM auth.wards
            WHERE  municipality_id =
                   current_setting('app.current_municipality_id', true)::UUID
        )
    );

-- Policy 3 — Province admin: SELECT only, all municipalities in their province
CREATE POLICY province_citizen_policy
    ON citizen_registry.citizens
    AS PERMISSIVE
    FOR SELECT
    TO province_role
    USING (
        ward_id IN (
            SELECT w.id
            FROM   auth.wards        w
            JOIN   auth.municipalities m ON w.municipality_id = m.id
            WHERE  m.province_id =
                   current_setting('app.current_province_id', true)::UUID
        )
    );

-- Policy 4 — Central admin: SELECT only, all citizens nationwide (no filter)
CREATE POLICY central_citizen_policy
    ON citizen_registry.citizens
    AS PERMISSIVE
    FOR SELECT
    TO central_role
    USING (true);

-- =============================================================================
-- 9. Protect audit_logs — append-only, no delete or update allowed
-- =============================================================================

REVOKE UPDATE, DELETE ON reporting.audit_logs FROM PUBLIC;
-- Grant reference table access to all roles
GRANT SELECT ON auth.wards TO ward_admin_role, local_body_role, province_role, central_role;
GRANT SELECT ON auth.municipalities TO ward_admin_role, local_body_role, province_role, central_role;
GRANT SELECT ON auth.provinces TO ward_admin_role, local_body_role, province_role, central_role;