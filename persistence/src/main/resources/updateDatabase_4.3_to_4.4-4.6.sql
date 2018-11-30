/*******************************************/
/*******************************************/
/**   updateDatabase_4.3_to_4.4-4.6.sql   **/
/*******************************************/
/*******************************************/


/**********************************************************************/
/* Removing order from registrar/archiver record                      */
/* https://github.com/NLCR/CZIDLO/issues/166                          */
/**********************************************************************/
ALTER TABLE ARCHIVER DROP COLUMN ITEM_ORDER;


/**********************************************************************/
/* Replacing tabs with spaces; trimming leading and trailing spaces   */
/* https://github.com/NLCR/CZIDLO/issues/164                          */
/**********************************************************************/
UPDATE ARCHIVER SET name=replace(name,chr(9),' ') WHERE name LIKE e'%\t%';
UPDATE ARCHIVER SET name=trim(name) WHERE name LIKE '\ %' OR name LIKE '%\ ';

UPDATE ARCHIVER SET description=replace(description,chr(9),' ') WHERE description LIKE e'%\t%';
UPDATE ARCHIVER SET description=trim(description) WHERE description LIKE '\ %' OR description LIKE '%\ ';

UPDATE IEIDENTIFIER SET idvalue=replace(idvalue,chr(9),' ') WHERE idvalue LIKE e'%\t%';
UPDATE IEIDENTIFIER SET idvalue=trim(idvalue) WHERE idvalue LIKE '\ %' OR idvalue LIKE '%\ ';

UPDATE INTELECTUALENTITY SET otheroriginator=replace(otheroriginator,chr(9),' ') WHERE otheroriginator LIKE e'%\t%';
UPDATE INTELECTUALENTITY SET otheroriginator=trim(otheroriginator) WHERE otheroriginator LIKE '\ %' OR otheroriginator LIKE '%\ ';

UPDATE INTELECTUALENTITY SET documenttype=replace(documenttype,chr(9),' ') WHERE documenttype LIKE e'%\t%';
UPDATE INTELECTUALENTITY SET documenttype=trim(documenttype) WHERE documenttype LIKE '\ %' OR documenttype LIKE '%\ ';

UPDATE INTELECTUALENTITY SET degreeawardinginstitution=replace(degreeawardinginstitution,chr(9),' ') WHERE degreeawardinginstitution LIKE e'%\t%';
UPDATE INTELECTUALENTITY SET degreeawardinginstitution=trim(degreeawardinginstitution) WHERE degreeawardinginstitution LIKE '\ %' OR degreeawardinginstitution LIKE '%\ ';

UPDATE DIGITALDOCUMENT SET financedfrom=replace(financedfrom,chr(9),' ') WHERE financedfrom LIKE e'%\t%';
UPDATE DIGITALDOCUMENT SET financedfrom=trim(financedfrom) WHERE financedfrom LIKE '\ %' OR financedfrom LIKE '%\ ';

UPDATE ORIGINATOR SET originvalue=replace(originvalue,chr(9),' ') WHERE originvalue LIKE e'%\t%';
UPDATE ORIGINATOR SET originvalue=trim(originvalue) WHERE originvalue LIKE '\ %' OR originvalue LIKE '%\ ';

UPDATE PUBLICATION SET place=replace(place,chr(9),' ') WHERE place LIKE e'%\t%';
UPDATE PUBLICATION SET place=trim(place) WHERE place LIKE '\ %' OR place LIKE '%\ ';

UPDATE PUBLICATION SET publisher=replace(publisher,chr(9),' ') WHERE publisher LIKE e'%\t%';
UPDATE PUBLICATION SET publisher=trim(publisher) WHERE publisher LIKE '\ %' OR publisher LIKE '%\ ';

UPDATE USERACCOUNT SET email=replace(email,chr(9),' ') WHERE email LIKE e'%\t%';
UPDATE USERACCOUNT SET email=trim(email) WHERE email LIKE '\ %' OR email LIKE '%\ ';


/*****************/
/*  New indexes  */
/*****************/
CREATE INDEX DIGITALINSTANCE_URL ON DIGITALINSTANCE (URL);


/*************************************************/
/*  New column DIGITALINSTANCE.ACCESSRESTRICTION */
/*************************************************/
ALTER TABLE DIGITALINSTANCE ADD COLUMN ACCESSRESTRICTION NUMERIC NOT NULL DEFAULT 0;


/**********************************/
/*          SEARCH cleanup        */
/**********************************/
/* INTELECTUAL ENTITY */
DROP TRIGGER IF EXISTS update_search_ie_preprocessed_intelectualentity_tg ON intelectualentity CASCADE;
DROP TRIGGER IF EXISTS update_search_ie_preprocessed_ieidentifier_tg ON ieidentifier CASCADE;
DROP TRIGGER IF EXISTS update_search_ie_preprocessed_originator_tg ON originator CASCADE;
DROP TRIGGER IF EXISTS update_search_ie_preprocessed_publication_tg ON publication CASCADE;
DROP TRIGGER IF EXISTS update_search_ie_preprocessed_sourcedocument_tg ON sourcedocument CASCADE;
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
DROP INDEX IF EXISTS search_ie_preprocessed_fulltext_idx;
DROP TABLE IF EXISTS search_ie_preprocessed;
DROP VIEW IF EXISTS search_ie_view;

/* DIGITAL DOCUMENT */
DROP TRIGGER IF EXISTS update_search_dd_preprocessed_digitaldocument_tg ON digitaldocument CASCADE;
DROP FUNCTION IF EXISTS update_search_dd_preprocessed_digitaldocument_tg() CASCADE;
DROP FUNCTION IF EXISTS update_search_dd_preprocessed_digitaldocument() CASCADE;
DROP FUNCTION IF EXISTS update_search_dd_preprocessed(NUMERIC,NUMERIC) CASCADE;
DROP INDEX IF EXISTS search_dd_preprocessed_fulltext_idx;
DROP TABLE IF EXISTS search_dd_preprocessed;
DROP VIEW IF EXISTS search_dd_view;

/* REGISTRAR-SCOPE ID */
DROP TRIGGER IF EXISTS update_search_rsi_preprocessed_registrarscopeid_tg ON registrarscopeid CASCADE;
DROP FUNCTION IF EXISTS update_search_rsi_preprocessed_registrarscopeid_tg() CASCADE;
DROP FUNCTION IF EXISTS update_search_rsi_preprocessed_registrarscopeid() CASCADE;
DROP FUNCTION IF EXISTS update_search_rsi_preprocessed(NUMERIC,NUMERIC,VARCHAR) CASCADE;
DROP INDEX IF EXISTS search_rsi_preprocessed_fulltext_idx;
DROP TABLE IF EXISTS search_rsi_preprocessed;
DROP VIEW IF EXISTS search_rsi_view;
