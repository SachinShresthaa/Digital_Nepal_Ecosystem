
CREATE TABLE IF NOT EXISTS citizen_gis (
    citizen_id              UUID PRIMARY KEY REFERENCES citizen(id) ON DELETE CASCADE,

    -- Denormalized for RLS — must always match citizen.ward_id at write time
    ward_id                 UUID NOT NULL REFERENCES ward(id),

    location                GEOMETRY(Point, 4326) NOT NULL,
    location_accuracy_m     SMALLINT,   -- device-reported GPS accuracy in meters
    elevation_m             SMALLINT,

    -- Optional manual entry MVP — not derived from any external data source
    risk_zone               VARCHAR(30) CHECK (risk_zone IN
                                 ('FLOOD','LANDSLIDE','EARTHQUAKE','FIRE','NONE')),
    road_type_to_highway    VARCHAR(30) CHECK (road_type_to_highway IN
                                 ('HIGHWAY','GRAVEL','DIRT','FOOTPATH','NONE')),

    -- Phase 2 columns — schema present, NEVER populated in MVP code paths
    dist_health_post_km     DECIMAL(6,2),
    time_health_post_min    SMALLINT,
    dist_school_km          DECIMAL(6,2),
    time_school_min         SMALLINT,
    dist_market_km          DECIMAL(6,2),
    dist_bank_km            DECIMAL(6,2),

    captured_by             UUID REFERENCES users(id),
    captured_at             TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Spatial index — required before any GIS query runs at scale
CREATE INDEX IF NOT EXISTS idx_gis_location_gist ON citizen_gis USING GIST(location);

-- Ward-scoped lookups (RLS-adjacent access pattern)
CREATE INDEX IF NOT EXISTS idx_gis_ward ON citizen_gis(ward_id);