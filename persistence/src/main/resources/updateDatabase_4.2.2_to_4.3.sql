/**********************************************************************/
/* Removing leading and trailing spaces in urn:nbn deactivation note. */
/* Also replacing empty strings with null.                            */
/* https://github.com/NLCR/CZIDLO/issues/142                          */
/**********************************************************************/

/* remove leading whitespaces */
UPDATE urnnbnsuccessors SET note=regexp_replace(note, '^\s*', '', 'g') WHERE note ~ '^\s';
/* remove traling whitespaces */
UPDATE urnnbnsuccessors SET note=regexp_replace(note, '\s*$', '', 'g') WHERE note ~ '\s$';
/* replace empty strings with null */
UPDATE urnnbnsuccessors SET note=null WHERE note='';


/****************************************************/
/* Removing now invalid registrar-scope identifiers */
/* https://github.com/NLCR/CZIDLO/issues/129        */
/****************************************************/
DELETE FROM registrarScopeId WHERE 
idValue !~ '^[0-9a-zA-Z]' OR 
idValue !~ '[0-9a-zA-Z]$' OR
type !~ '^[0-9a-zA-Z]' OR 
type !~ '[0-9a-zA-Z]$' OR 
char_length(idValue) > 60 OR 
char_length(type) < 2 OR 
char_length(type) > 20;

/* remove search-related indexes,tables, views, functions, triggers from previous version */
DROP FUNCTION IF EXISTS ie_title_update_trigger() CASCADE;
DROP FUNCTION IF EXISTS ie_title_update_trigger_function() CASCADE;
DROP FUNCTION IF EXISTS ie_title_update(NUMERIC) CASCADE;
DROP view IF EXISTS ie_title_view;
DROP index IF EXISTS ie_title_fulltext_idx;
DROP table IF EXISTS ie_title;


/*************************/
/*        SEARCH         */
/*************************/

/* If language plgsql is not installed, install it by this: CREATE OR REPLACE LANGUAGE plpgsql;*/ 
/* Language instalation must be run by database owner. */ 

/*******************************/
/* SEARCH - intelectual entity */
/*******************************/

/* cleanup */
DROP INDEX IF EXISTS search_ie_preprocessed_fulltext_idx;
DROP TABLE IF EXISTS search_ie_preprocessed;
DROP VIEW IF EXISTS search_ie_view;
DROP FUNCTION IF EXISTS update_search_ie_preprocessed_intelectualentity_tg() CASCADE;
DROP FUNCTION IF EXISTS update_search_ie_preprocessed_ieidentifier_tg() CASCADE;
DROP FUNCTION IF EXISTS update_search_ie_preprocessed_originator_tg() CASCADE;
DROP FUNCTION IF EXISTS update_search_ie_preprocessed_publication_tg() CASCADE;
DROP FUNCTION IF EXISTS update_search_ie_preprocessed_sourcedocument_tg() CASCADE;
DROP FUNCTION IF EXISTS update_search_ie_preprocessed_intelectualentity() CASCADE;
DROP FUNCTION IF EXISTS update_search_ie_preprocessed_ieidentifier() CASCADE;
DROP FUNCTION IF EXISTS update_search_ie_preprocessed_originator() CASCADE;
DROP FUNCTION IF EXISTS update_search_ie_preprocessed_publication() CASCADE;
DROP FUNCTION IF EXISTS update_search_ie_preprocessed_sourcedocument() CASCADE;
DROP FUNCTION IF EXISTS update_search_ie_preprocessed(NUMERIC) CASCADE;

/* create preprocessed table */
CREATE TABLE search_ie_preprocessed 
(
   ieId NUMERIC NOT NULL,
   searchable VARCHAR,
   PRIMARY KEY (ieId)
) WITHOUT OIDS;

/* preprocessed table index*/
CREATE INDEX search_ie_preprocessed_fulltext_idx ON search_ie_preprocessed USING gin(to_tsvector('simple', lower(searchable)));

/* view - source for data for preprocessd table */
CREATE OR REPLACE VIEW search_ie_view AS
SELECT
/* intelectualentity (id) */
   intEnt.id AS ieid,
   '' || intEnt.id || 
/* ieidentifier */   
   CASE WHEN title.value IS NULL THEN ''
        ELSE ' ' || title.value
   END ||
   CASE WHEN subtitle.value IS NULL THEN '' 
        ELSE ' ' || subtitle.value
   END ||
   CASE WHEN volume.value IS NULL THEN '' 
        ELSE ' ' || volume.value
   END ||
   CASE WHEN issue.value IS NULL THEN '' 
        ELSE ' ' || issue.value
   END ||
   CASE WHEN ccnb.value IS NULL THEN ''
        ELSE ' ' || ccnb.value
   END ||
   CASE WHEN isbn.value IS NULL THEN ''
        ELSE ' ' || isbn.value
   END ||
   CASE WHEN issn.value IS NULL THEN ''
        ELSE ' ' || issn.value
   END ||
/* originator */
   CASE WHEN originator.originValue IS NULL THEN '' 
        ELSE ' ' || originator.originValue
   END ||
/* publisher */
   CASE WHEN publication.pyear IS NULL THEN ''
        ELSE ' ' || publication.pyear
   END ||
   CASE WHEN publication.place IS NULL THEN ''
        ELSE ' ' || publication.place
   END ||
   CASE WHEN publication.publisher IS NULL THEN ''
        ELSE ' ' || publication.publisher
   END ||
/* intelectualentity (other) */
   CASE WHEN intEnt.otherOriginator IS NULL THEN ''
        ELSE ' ' || intEnt.otherOriginator
   END ||
   CASE WHEN intEnt.degreeAwardingInstitution IS NULL THEN ''
        ELSE ' ' || intEnt.degreeAwardingInstitution
   END ||
/* source document */
   CASE WHEN srcDoc.title IS NULL THEN ''
        ELSE ' ' || srcDoc.title
   END ||
   CASE WHEN srcDoc.volumetitle IS NULL THEN ''
        ELSE ' ' || srcDoc.volumetitle
   END ||
   CASE WHEN srcDoc.issuetitle IS NULL THEN ''
        ELSE ' ' || srcDoc.issuetitle
   END ||
   CASE WHEN srcDoc.ccnb IS NULL THEN ''
        ELSE ' ' || srcDoc.ccnb
   END ||
   CASE WHEN srcDoc.isbn IS NULL THEN ''
        ELSE ' ' || srcDoc.isbn
   END ||
   CASE WHEN srcDoc.issn IS NULL THEN ''
        ELSE ' ' || srcDoc.issn
   END ||
   CASE WHEN srcDoc.otherid IS NULL THEN ''
        ELSE ' ' || srcDoc.otherid
   END ||
   CASE WHEN srcDoc.publisher IS NULL THEN ''
        ELSE ' ' || srcDoc.publisher
   END ||
   CASE WHEN srcDoc.publicationplace IS NULL THEN ''
        ELSE ' ' || srcDoc.publicationplace
   END ||
   CASE WHEN srcDoc.publicationyear IS NULL THEN ''
        ELSE ' ' || srcDoc.publicationyear
   END
AS searchable
FROM
   (SELECT id, otherOriginator, degreeAwardingInstitution FROM intelectualentity) AS intEnt LEFT OUTER JOIN
   (SELECT intelectualentityid AS ieId, originValue FROM originator) AS originator on intEnt.id = originator.ieId LEFT OUTER JOIN
   (SELECT intelectualentityid AS ieId, pyear, place, publisher FROM publication) AS publication on intEnt.id = publication.ieId LEFT OUTER JOIN
   (SELECT intelectualentityid AS ieId, title, volumeTitle, issueTitle, ccnb, isbn, issn, otherId, publisher, publicationPlace, publicationYear FROM sourceDocument) AS srcDoc on intEnt.id = srcDoc.ieId LEFT OUTER JOIN
   (SELECT intelectualentityid AS ieId, idvalue AS value FROM ieidentifier WHERE type = 'TITLE') AS title on intEnt.id = title.ieId LEFT OUTER JOIN
   (SELECT intelectualentityid AS ieId, idvalue AS value FROM ieidentifier WHERE type = 'SUB_TITLE') AS subtitle ON intEnt.id = subtitle.ieId LEFT OUTER JOIN
   (SELECT intelectualentityid AS ieId, idvalue AS value FROM ieidentifier WHERE type = 'VOLUME_TITLE') AS volume ON intEnt.id = volume.ieId LEFT OUTER JOIN
   (SELECT intelectualentityid AS ieId, idvalue AS value FROM ieidentifier WHERE type = 'ISSUE_TITLE') AS issue ON intEnt.id = issue.ieId LEFT OUTER JOIN
   (SELECT intelectualentityid AS ieId, idvalue AS value FROM ieidentifier WHERE type = 'CCNB') AS ccnb ON intEnt.id = ccnb.ieId LEFT OUTER JOIN
   (SELECT intelectualentityid AS ieId, idvalue AS value FROM ieidentifier WHERE type = 'ISBN') AS isbn ON intEnt.id = isbn.ieId LEFT OUTER JOIN
   (SELECT intelectualentityid AS ieId, idvalue AS value FROM ieidentifier WHERE type = 'ISSN') AS issn ON intEnt.id = issn.ieId
;

/* function to update preprocessed table record by ieId */
CREATE OR REPLACE FUNCTION update_search_ie_preprocessed(NUMERIC) RETURNS void AS $BODY$
BEGIN
   IF (EXISTS (SELECT ieId FROM search_ie_view WHERE ieId = $1)) THEN
      IF (EXISTS (SELECT ieId FROM search_ie_preprocessed WHERE ieId = $1)) THEN
         UPDATE search_ie_preprocessed SET searchable = (SELECT searchable FROM search_ie_view WHERE ieId = $1) WHERE ieId = $1;
      ELSE
         INSERT INTO search_ie_preprocessed (SELECT * FROM search_ie_view WHERE ieId = $1);
      END IF;
   ELSE
      DELETE FROM search_ie_preprocessed WHERE ieId = $1;
   END IF;
END;
$BODY$
LANGUAGE plpgsql;

/* function to update preprocessed table record after intelectualEntity change */
CREATE OR REPLACE FUNCTION update_search_ie_preprocessed_intelectualentity() RETURNS TRIGGER AS $BODY$
   BEGIN
      IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
         EXECUTE update_search_ie_preprocessed(NEW.id);
      ELSIF (TG_OP = 'DELETE') THEN
         EXECUTE update_search_ie_preprocessed(OLD.id);
      END IF;
      RETURN NULL;
   END;
$BODY$
LANGUAGE plpgsql;

/* function to update preprocessed table record after ieIdentifier change */
CREATE OR REPLACE FUNCTION update_search_ie_preprocessed_ieidentifier() RETURNS TRIGGER AS $BODY$
   BEGIN
      IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
         EXECUTE update_search_ie_preprocessed(NEW.intelectualentityid);
      ELSIF (TG_OP = 'DELETE') THEN
         EXECUTE update_search_ie_preprocessed(OLD.intelectualentityid);
      END IF;
      RETURN NULL;
   END;
$BODY$
LANGUAGE plpgsql;

/* function to update preprocessed table record after originator change */
CREATE OR REPLACE FUNCTION update_search_ie_preprocessed_originator() RETURNS TRIGGER AS $BODY$
   BEGIN
      IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
         EXECUTE update_search_ie_preprocessed(NEW.intelectualentityid);
      ELSIF (TG_OP = 'DELETE') THEN
         EXECUTE update_search_ie_preprocessed(OLD.intelectualentityid);
      END IF;
      RETURN NULL;
   END;
$BODY$
LANGUAGE plpgsql;

/* function to update preprocessed table record after publication change */
CREATE OR REPLACE FUNCTION update_search_ie_preprocessed_publication() RETURNS TRIGGER AS $BODY$
   BEGIN
      IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
         EXECUTE update_search_ie_preprocessed(NEW.intelectualentityid);
      ELSIF (TG_OP = 'DELETE') THEN
         EXECUTE update_search_ie_preprocessed(OLD.intelectualentityid);
      END IF;
      RETURN NULL;
   END;
$BODY$
LANGUAGE plpgsql;

/* function to update preprocessed table record after sourceDocument change */
CREATE OR REPLACE FUNCTION update_search_ie_preprocessed_sourcedocument() RETURNS TRIGGER AS $BODY$
   BEGIN
      IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
         EXECUTE update_search_ie_preprocessed(NEW.intelectualentityid);
      ELSIF (TG_OP = 'DELETE') THEN
         EXECUTE update_search_ie_preprocessed(OLD.intelectualentityid);
      END IF;
      RETURN NULL;
   END;
$BODY$
LANGUAGE plpgsql;

/* triggers to update preprocessed table record after various changes */
CREATE TRIGGER update_search_ie_preprocessed_intelectualentity_tg AFTER INSERT OR UPDATE OR DELETE ON intelectualentity FOR EACH ROW EXECUTE PROCEDURE update_search_ie_preprocessed_intelectualentity();
CREATE TRIGGER update_search_ie_preprocessed_ieidentifier_tg AFTER INSERT OR UPDATE OR DELETE ON ieidentifier FOR EACH ROW EXECUTE PROCEDURE update_search_ie_preprocessed_ieidentifier();
CREATE TRIGGER update_search_ie_preprocessed_originator_tg AFTER INSERT OR UPDATE OR DELETE ON originator FOR EACH ROW EXECUTE PROCEDURE update_search_ie_preprocessed_originator();
CREATE TRIGGER update_search_ie_preprocessed_publication_tg AFTER INSERT OR UPDATE OR DELETE ON publication FOR EACH ROW EXECUTE PROCEDURE update_search_ie_preprocessed_publication();
CREATE TRIGGER update_search_ie_preprocessed_sourcedocument_tg AFTER INSERT OR UPDATE OR DELETE ON sourcedocument FOR EACH ROW EXECUTE PROCEDURE update_search_ie_preprocessed_sourcedocument();

/* initial filling table search_ie_preprocessed */
SELECT update_search_ie_preprocessed(intelectualentityid) FROM digitaldocument ;

/*******************************/
/* SEARCH - digital document   */
/*******************************/

/* cleanup */ 
DROP INDEX IF EXISTS search_dd_preprocessed_fulltext_idx;
DROP TABLE IF EXISTS search_dd_preprocessed;
DROP VIEW IF EXISTS search_dd_view;
DROP FUNCTION IF EXISTS update_search_dd_preprocessed_digitaldocument_tg() CASCADE;
DROP FUNCTION IF EXISTS update_search_dd_preprocessed_digitaldocument() CASCADE;
DROP FUNCTION IF EXISTS update_search_dd_preprocessed(NUMERIC,NUMERIC) CASCADE;

/* create preprocessed table */ 
CREATE TABLE search_dd_preprocessed 
(
   ieId NUMERIC NOT NULL,
   ddId NUMERIC NOT NULL,
   searchable VARCHAR,
   PRIMARY KEY (ieId, ddId)
) WITHOUT OIDS;

/* preprocessed table index*/
CREATE INDEX search_dd_preprocessed_fulltext_idx ON search_dd_preprocessed USING gin(to_tsvector('simple', lower(searchable)));

/* view - source for data for preprocessd table */
CREATE OR REPLACE VIEW search_dd_view AS
SELECT
   digitalDocument.intelectualEntityId AS ieId,
   digitalDocument.id AS ddId,
   '' || digitaldocument.id || 
   CASE WHEN digitaldocument.contractNumber IS NULL THEN '' 
        ELSE ' ' || digitaldocument.contractNumber 
   END
   AS searchable
FROM digitalDocument;

/* function to update preprocessed table record by ieId and ddId */
CREATE OR REPLACE FUNCTION update_search_dd_preprocessed(NUMERIC, NUMERIC) RETURNS void AS $BODY$
BEGIN
   IF (EXISTS (SELECT ddId FROM search_dd_view WHERE ieId = $1 AND ddId = $2)) THEN
      IF (EXISTS (SELECT ddId FROM search_dd_preprocessed WHERE ieId = $1 AND ddId = $2)) THEN
         UPDATE search_dd_preprocessed SET searchable = (SELECT searchable FROM search_dd_view WHERE ieId = $1 AND ddId = $2) WHERE ieId = $1 AND ddId = $2;
      ELSE
         INSERT INTO search_dd_preprocessed (SELECT * FROM search_dd_view WHERE ieId = $1 AND ddId = $2);
      END IF;
   ELSE
      DELETE FROM search_dd_preprocessed WHERE ieId = $1 AND ddId = $2;
   END IF;
END;
$BODY$
LANGUAGE plpgsql;

/* function to update preprocessed table record after digitalDocument change */
CREATE OR REPLACE FUNCTION update_search_dd_preprocessed_digitaldocument() RETURNS TRIGGER AS $BODY$
   BEGIN
      IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
         EXECUTE update_search_dd_preprocessed(NEW.intelectualentityid, NEW.id);
      ELSIF (TG_OP = 'DELETE') THEN
         EXECUTE update_search_dd_preprocessed(OLD.intelectualentityid, OLD.id);
      END IF;
      RETURN NULL;
   END;
$BODY$
LANGUAGE plpgsql;

/* triggers to update preprocessed table record after various changes */
CREATE TRIGGER update_search_dd_preprocessed_digitaldocument_tg AFTER INSERT OR UPDATE OR DELETE ON digitaldocument FOR EACH ROW EXECUTE PROCEDURE update_search_dd_preprocessed_digitaldocument();

/* initial filling table search_ie_preprocessed */
SELECT update_search_dd_preprocessed(intelectualentityid, id)  FROM digitaldocument ;

/*******************************/
/* SEARCH - registrar-scope id */
/*******************************/

/* cleanup */
DROP INDEX IF EXISTS search_rsi_preprocessed_fulltext_idx;
DROP TABLE IF EXISTS search_rsi_preprocessed;
DROP VIEW IF EXISTS search_rsi_view;
DROP FUNCTION IF EXISTS update_search_rsi_preprocessed_registrarscopeid_tg() CASCADE;
DROP FUNCTION IF EXISTS update_search_rsi_preprocessed_registrarscopeid() CASCADE;
DROP FUNCTION IF EXISTS update_search_rsi_preprocessed(NUMERIC,NUMERIC) CASCADE;

/* create preprocessed table */ 
CREATE TABLE search_rsi_preprocessed
(
   regId NUMERIC NOT NULL,
   ieId NUMERIC NOT NULL,
   ddId NUMERIC NOT NULL,
   type VARCHAR NOT NULL,
   searchable VARCHAR,
   PRIMARY KEY (regId, ddId, type)
) WITHOUT OIDS;

/* preprocessed table index*/
CREATE INDEX search_rsi_preprocessed_fulltext_idx ON search_rsi_preprocessed USING gin(to_tsvector('simple', lower(searchable)));

/* view - source for data for preprocessd table */
CREATE OR REPLACE VIEW search_rsi_view AS
SELECT
   registrarscopeid.registrarId AS regId,
   digitaldocument.intelectualEntityId AS ieId,
   registrarscopeid.digitaldocumentid AS ddId,
   registrarscopeid.type AS type,
   COALESCE(registrarscopeid.idValue, '') AS searchable
FROM registrarscopeid INNER JOIN digitaldocument ON registrarscopeid.digitalDocumentId = digitaldocument.id;

/* function to update preprocessed table record by registarId, ddId, idType */
CREATE OR REPLACE FUNCTION update_search_rsi_preprocessed(NUMERIC, NUMERIC, VARCHAR) RETURNS void AS $BODY$
BEGIN
   IF (EXISTS (SELECT ieId FROM search_rsi_view WHERE regId=$1 AND ddId=$2 AND type=$3)) THEN
      IF (EXISTS (SELECT ieId FROM search_rsi_preprocessed WHERE regId=$1 AND ddId=$2 AND type=$3)) THEN
         UPDATE search_rsi_preprocessed SET searchable = (SELECT searchable FROM search_rsi_view WHERE regId=$1 AND ddId=$2 AND type=$3) WHERE regId=$1 AND ddId=$2 AND type=$3;
      ELSE
         INSERT INTO search_rsi_preprocessed (SELECT * FROM search_rsi_view WHERE regId=$1 AND ddId=$2 AND type=$3);
      END IF;
   ELSE
      DELETE FROM search_rsi_preprocessed WHERE regId=$1 AND ddId=$2 AND type=$3;
   END IF;
END;
$BODY$
LANGUAGE plpgsql;

/* function to update preprocessed table record after registrarScopeId change */
CREATE OR REPLACE FUNCTION update_search_rsi_preprocessed_registrarscopeid() RETURNS TRIGGER AS $BODY$
   BEGIN
      IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
         EXECUTE update_search_rsi_preprocessed(NEW.registrarId, NEW.digitaldocumentid, NEW.type);
      ELSIF (TG_OP = 'DELETE') THEN
         EXECUTE update_search_rsi_preprocessed(OLD.registrarId, OLD.digitaldocumentid, OLD.type);
      END IF;
      RETURN NULL;
   END;
$BODY$
LANGUAGE plpgsql;

/* triggers to update preprocessed table record after various changes */
CREATE TRIGGER update_search_rsi_preprocessed_registrarscopeid_tg AFTER INSERT OR UPDATE OR DELETE ON registrarscopeid FOR EACH ROW EXECUTE PROCEDURE update_search_rsi_preprocessed_registrarscopeid();

/* initial filling table search_rsi_preprocessed */
SELECT update_search_rsi_preprocessed(registrarId, digitalDocumentId, type) FROM registrarScopeId;

/**************************************/
/* Statistics for URN:NBN assignments */
/**************************************/

/* cleanup */
DROP INDEX IF EXISTS urnnbn_registrar_code_year_month_active_idx;
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

/* index for efficient view*/
CREATE INDEX urnnbn_registrar_code_year_month_active_idx ON urnnbn(registrarCode, to_year(registered), to_month(registered), active);

/* view - source for data for preprocessd table */
CREATE OR REPLACE VIEW urnnbn_assignment_statistics_view AS
SELECT 
	registrarCode AS registrarCode,
	to_year(registered) AS year,
	to_month(registered) AS month,
	active AS active,
	count(*) AS sum FROM UrnNbn
GROUP BY registrarCode, year, month, active;

/* function to update preprocessed table record by registrarCode, year, month, activity */
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
         EXECUTE update_urnnbn_assignment(NEW.registrarCode, to_year(NEW.registered), to_year(NEW.registered),true);
         EXECUTE update_urnnbn_assignment(NEW.registrarCode, to_year(NEW.registered), to_year(NEW.registered),false);
      ELSIF (TG_OP = 'DELETE') THEN
         EXECUTE update_urnnbn_assignment(OLD.registrarCode, to_year(OLD.registered), to_year(OLD.registered),true);
         EXECUTE update_urnnbn_assignment(OLD.registrarCode, to_year(OLD.registered), to_year(OLD.registered),false);
      END IF;
      RETURN NULL;
   END;
$BODY$
LANGUAGE plpgsql;

/* triggers to update preprocessed table record after various changes */
CREATE TRIGGER update_urnnbn_assignment_tg AFTER INSERT OR UPDATE  ON UrnNbn FOR EACH ROW EXECUTE PROCEDURE update_urnnbn_assignment();

/* initial filling table search_rsi_preprocessed */
INSERT INTO urnnbn_assignment_statistics_preprocessed (SELECT * FROM urnnbn_assignment_statistics_view);

/**************************************/
/* Statistics for URN:NBN resolvations */
/**************************************/

CREATE TABLE urnnbn_resolvation_statistics
(
	registrarCode VARCHAR NOT NULL,
	year NUMERIC NOT NULL,
	month NUMERIC NOT NULL,
	sum NUMERIC NOT NULL,
	PRIMARY KEY (registrarCode, year, month)
) WITHOUT OIDS;
