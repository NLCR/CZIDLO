--
-- Description: Create table urnnbn_resolvation and populate it based on urnnbn_resolvation_statistics
--              Each row in urnnbn_resolvation_statistics with a count 'sum' will generate 'sum' rows
--              in urnnbn_resolvation with the same registrarcode and resolved timestamp, and documentcode set to NULL.
--
-- Usage: psql -d czidlo_core -U czidlo -v ON_ERROR_STOP=1 -f init_table_urnnbn_resolvation.sql

DROP TABLE IF EXISTS urnnbn_resolvation;

CREATE TABLE urnnbn_resolvation (
  id SERIAL PRIMARY KEY,
  registrarcode VARCHAR(6) NOT NULL,
  documentcode VARCHAR(6),
  resolved TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

WITH RECURSIVE
  RowGenerator AS (
    SELECT
      us.registrarcode,
      make_timestamp(
        us.year::INTEGER,
        us.month::INTEGER + 1,
        1,
        12, 0, 0.0
      ) AS resolved,
      us.sum AS count
    FROM
      urnnbn_resolvation_statistics us

    UNION ALL

    SELECT
      rg.registrarcode,
      rg.resolved,
      rg.count - 1
    FROM
      RowGenerator rg
    WHERE
      rg.count > 1
  )

INSERT INTO
  urnnbn_resolvation (registrarcode, resolved)
SELECT
  registrarcode,
  resolved
FROM
  RowGenerator;


-- Drop old table urnnbn_resolvation_statistics
DROP TABLE IF EXISTS urnnbn_resolvation_statistics;
DROP TABLE IF EXISTS urnnbn_assignment_statistics_preprocessed;
DROP VIEW IF EXISTS urnnbn_assignment_statistics_view;