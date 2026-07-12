-- Family tree linking table

CREATE TABLE IF NOT EXISTS family_link (
    id                      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),

    -- The citizen who owns this link
    citizen_id              UUID        NOT NULL REFERENCES citizen(id),

    -- FATHER / MOTHER / SPOUSE / CHILD
    relation_type           VARCHAR(30) NOT NULL,

    -- Related citizen if already registered — NULL if PENDING
    related_citizen_id      UUID        REFERENCES citizen(id),

    -- Name as provided during registration
    related_name_text       VARCHAR(300),

    -- Normalized citizenship number for auto-linking
    -- Matched against citizen.citizenship_no_norm when related person registers
    related_citizenship_no  VARCHAR(100),

    -- PENDING / LINKED / UNRESOLVABLE
    link_status             VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    created_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================================
-- INDEXES
-- ============================================================================

-- Fast lookup of all links for a citizen (family tree view)
CREATE INDEX idx_family_link_citizen ON family_link(citizen_id);

-- Fast lookup of links pointing to a specific related citizen
CREATE INDEX idx_family_link_related ON family_link(related_citizen_id);

-- Fast lookup of PENDING links by citizenship number
-- Used by resolvePendingLinks() on every new registration
CREATE INDEX idx_family_link_cit_norm ON family_link(related_citizenship_no)
    WHERE link_status = 'PENDING';
