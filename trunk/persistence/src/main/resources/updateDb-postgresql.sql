/* SQL script for updating database of Resolver from version 2 to version 3 */

/* rename attribute for consistency (mode BY_REGISTRAR) */
ALTER TABLE REGISTRAR RENAME COLUMN ALLOWEDTOREGISTERFREEURNNBN TO ALLOWEDREGISTRATIONMODEBYREGISTRAR;

/* add new attribute to state whether registration mode BY_RESOLVER is allowed for registrar */
ALTER TABLE REGISTRAR ADD COLUMN ALLOWEDREGISTRATIONMODEBYRESOLVER BOOLEAN;
/* set false to all existing registars */
UPDATE REGISTRAR SET ALLOWEDREGISTRATIONMODEBYRESOLVER = 'false';
/* add NOT NULL constraint */
ALTER TABLE REGISTRAR ALTER COLUMN ALLOWEDREGISTRATIONMODEBYRESOLVER SET NOT NULL;

/* add new attribute to state whether registration mode BY_RESERVATION is allowed for registrar */
ALTER TABLE REGISTRAR ADD COLUMN ALLOWEDREGISTRATIONMODEBYRESERVATION BOOLEAN;
/* set false to all existing registars */
UPDATE REGISTRAR SET ALLOWEDREGISTRATIONMODEBYRESERVATION = 'false';
/* add NOT NULL constraint */
ALTER TABLE REGISTRAR ALTER COLUMN ALLOWEDREGISTRATIONMODEBYRESERVATION SET NOT NULL;


/* new attribute ACTIVE for urn:nbn */
ALTER TABLE URNNBN ADD COLUMN ACTIVE BOOLEAN;
/* set true to all existing urn:nbn */
UPDATE URNNBN SET ACTIVE = 'true';
/* add NOT NULL constraint */
ALTER TABLE URNNBN  ALTER COLUMN ACTIVE SET NOT NULL;

/* new table for predecessor - successor relationship */
CREATE TABLE URNNBNSUCCESSORS
(
	PREDECESSORREGCODE VARCHAR NOT NULL,
	PREDECESSORDOCCODE VARCHAR NOT NULL,
	SUCCESSORREGCODE VARCHAR NOT NULL,
	SUCCESSORDOCCODE VARCHAR NOT NULL
) WITHOUT OIDS;
/* indecies for new table */
CREATE INDEX URNNBN_PREDECESSOR ON URNNBNSUCCESSORS (PREDECESSORREGCODE, PREDECESSORDOCCODE);
CREATE INDEX URNNBN_SUCCESSOR ON URNNBNSUCCESSORS (SUCCESSORREGCODE, SUCCESSORDOCCODE);
CREATE INDEX URNNBN_PREDECESSOR_SUCCESSOR ON URNNBNSUCCESSORS (PREDECESSORREGCODE, PREDECESSORDOCCODE, SUCCESSORREGCODE, SUCCESSORDOCCODE);

/* table ddIdentifier renamed */
ALTER TABLE DDIDENTIFIER RENAME TO REGISTRARSCOPEID;

/* change of urn:nbn datastamp attributes */
ALTER TABLE URNNBN RENAME COLUMN CREATED TO REGISTERED;
ALTER TABLE URNNBN DROP COLUMN MODIFIED;
ALTER TABLE URNNBN ADD COLUMN RESERVED TIMESTAMP;
ALTER TABLE URNNBN ADD COLUMN DEACTIVATED TIMESTAMP;

/* modified is renamed and does can be null */
ALTER TABLE DIGITALINSTANCE RENAME COLUMN MODIFIED TO DEACTIVATED;
ALTER TABLE DIGITALINSTANCE ALTER COLUMN DEACTIVATED DROP NOT NULL;
UPDATE DIGITALINSTANCE SET DEACTIVATED=null WHERE ACTIV='true';
