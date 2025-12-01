--
-- Description: Create table urnnbn_resolvations and populate it based on urnnbn_resolvation_statistics
--              Each row in urnnbn_resolvation_statistics with a count 'sum' will generate 'sum' rows
--              in urnnbn_resolvations with the same registrarcode and resolved timestamp, and documentcode set to NULL.
--
-- Usage: psql -d czidlo_core -v ON_ERROR_STOP=1 -f init_table_urnnbn_resolvations.sql

DROP TABLE IF EXISTS urnnbn_resolvations;

CREATE TABLE urnnbn_resolvations (
  registrarcode VARCHAR(6) NOT NULL,
  resolved TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  documentcode VARCHAR(6)
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
  urnnbn_resolvations
SELECT
  registrarcode,
  resolved,
  NULL
FROM
  RowGenerator
