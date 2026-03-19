-- Migration: Migrate from JSONB to pgvector for vector storage
-- Description: Enables pgvector extension, drops old JSONB column, adds native vector column

CREATE EXTENSION IF NOT EXISTS vector;

ALTER TABLE evaluation_vectors DROP COLUMN IF EXISTS vector_data;
ALTER TABLE evaluation_vectors ADD COLUMN embedding vector;

-- Delete seed data from V3 (fake vectors with inconsistent dimensions)
-- Real vectors will be regenerated via POST /vectors/initialize
DELETE FROM evaluation_vectors;
