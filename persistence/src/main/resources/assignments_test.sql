/**************************************/
/* Statistics for URN:NBN assignments */
/**************************************/

/* cleanup */
DROP INDEX IF EXISTS urnnbn_registrar_code_year_month_active_idx;
DROP INDEX IF EXISTS urnnbn_assignment_statistics_preprocessed_registrar_idx;
DROP INDEX IF EXISTS urnnbn_assignment_statistics_preprocessed_active_idx;
DROP INDEX IF EXISTS urnnbn_assignment_statistics_preprocessed_registrar_active_idx;
DROP TABLE IF EXISTS urnnbn_assignment_statistics_preprocessed;
DROP VIEW IF EXISTS urnnbn_assignment_statistics_view;
DROP FUNCTION IF EXISTS update_urnnbn_assignment_tg() CASCADE;
DROP FUNCTION IF EXISTS update_urnnbn_assignment() CASCADE;
DROP FUNCTION IF EXISTS update_urnnbn_assignment(VARCHAR, TIMESTAMP) CASCADE;
DROP FUNCTION IF EXISTS update_urnnbn_assignment(VARCHAR, NUMERIC,NUMERIC, BOOLEAN) CASCADE;


/* new functions (needed for immutable function for indecies) */
DROP FUNCTION IF EXISTS to_month(TIMESTAMP) CASCADE;
DROP FUNCTION IF EXISTS to_year(TIMESTAMP) CASCADE;

CREATE OR REPLACE FUNCTION to_month(TIMESTAMP) 
  RETURNS NUMERIC
AS
$BODY$
    SELECT to_number(to_char($1,'MM'),'99')
$BODY$
LANGUAGE sql
IMMUTABLE;

CREATE OR REPLACE FUNCTION to_year(TIMESTAMP) 
  RETURNS NUMERIC
AS
$BODY$
    SELECT to_number(to_char($1,'YYYY'),'9999')
$BODY$
LANGUAGE sql
IMMUTABLE;

/* create preprocessed table */ 
CREATE TABLE urnnbn_assignment_statistics_preprocessed
(
   registrarCode VARCHAR NOT NULL,
   year NUMERIC NOT NULL,
   month NUMERIC NOT NULL,
   active BOOLEAN NOT NULL,
   sum NUMERIC NOT NULL,
   PRIMARY KEY (registrarCode, year, month, active)
) WITHOUT OIDS;

/* preprocessed table index*/
CREATE INDEX urnnbn_registrar_code_year_month_active_idx ON urnnbn(registrarCode, to_year(registered), to_month(registered), active);
CREATE INDEX urnnbn_assignment_statistics_preprocessed_registrar_idx ON urnnbn_assignment_statistics_preprocessed (registrarCode);
CREATE INDEX urnnbn_assignment_statistics_preprocessed_active_idx ON urnnbn_assignment_statistics_preprocessed (active);
CREATE INDEX urnnbn_assignment_statistics_preprocessed_registrar_active_idx ON urnnbn_assignment_statistics_preprocessed (registrarCode, active);

/* view - source for data for preprocessd table */
CREATE OR REPLACE VIEW urnnbn_assignment_statistics_view AS
SELECT 
	registrarCode AS registrarCode,
	to_year(registered) AS year,
	to_month(registered) AS month,
	active AS active,
	count(*) AS sum FROM UrnNbn
GROUP BY registrarCode, year, month, active;

--function to update preprocessed table record by registrarCode, year, month, activity
CREATE OR REPLACE FUNCTION update_urnnbn_assignment(VARCHAR, NUMERIC, NUMERIC, BOOLEAN) RETURNS void AS $BODY$
BEGIN
   IF (EXISTS (SELECT sum FROM urnnbn_assignment_statistics_view WHERE registrarCode=$1 AND year=$2 AND month=$3 AND active=$4)) THEN
      IF (EXISTS (SELECT sum FROM urnnbn_assignment_statistics_preprocessed WHERE registrarCode=$1 AND year=$2 AND month=$3 AND active=$4)) THEN
         UPDATE urnnbn_assignment_statistics_preprocessed 
            SET sum = (SELECT sum FROM urnnbn_assignment_statistics_view WHERE registrarCode=$1 AND year=$2 AND month=$3 AND active=$4)
         WHERE registrarCode=$1 AND year=$2 AND month=$3 AND active=$4;
      ELSE
         INSERT INTO urnnbn_assignment_statistics_preprocessed 
         (SELECT * FROM urnnbn_assignment_statistics_view WHERE registrarCode=$1 AND year=$2 AND month=$3 AND active=$4);
      END IF;
   ELSE
      DELETE FROM urnnbn_assignment_statistics_preprocessed WHERE registrarCode=$1 AND year=$2 AND month=$3 AND active=$4;
   END IF;
END;
$BODY$
LANGUAGE plpgsql;

/* function to update preprocessed table record by registrarCode, timestamp */
CREATE OR REPLACE FUNCTION update_urnnbn_assignment(VARCHAR, TIMESTAMP) RETURNS void AS $BODY$
BEGIN
  EXECUTE update_urnnbn_assignment($1, to_year($2), to_month($2),true);
  EXECUTE update_urnnbn_assignment($1, to_year($2), to_month($2),false);
END;
$BODY$
LANGUAGE plpgsql;


/* function to update preprocessed table record after registrarScopeId change */
CREATE OR REPLACE FUNCTION update_urnnbn_assignment() RETURNS TRIGGER AS $BODY$
   BEGIN
      IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
         EXECUTE update_urnnbn_assignment(NEW.registrarCode, to_year(NEW.registered), to_month(NEW.registered),true);
         EXECUTE update_urnnbn_assignment(NEW.registrarCode, to_year(NEW.registered), to_month(NEW.registered),false);
      ELSIF (TG_OP = 'DELETE') THEN
         EXECUTE update_urnnbn_assignment(OLD.registrarCode, to_year(OLD.registered), to_month(OLD.registered),true);
         EXECUTE update_urnnbn_assignment(OLD.registrarCode, to_year(OLD.registered), to_month(OLD.registered),false);
      END IF;
      RETURN NULL;
   END;
$BODY$
LANGUAGE plpgsql;

/* triggers to update preprocessed table record after various changes */
CREATE TRIGGER update_urnnbn_assignment_tg AFTER INSERT OR UPDATE  ON UrnNbn FOR EACH ROW EXECUTE PROCEDURE update_urnnbn_assignment();

/* initial filling table search_rsi_preprocessed */
INSERT INTO urnnbn_assignment_statistics_preprocessed (SELECT * FROM urnnbn_assignment_statistics_view);

