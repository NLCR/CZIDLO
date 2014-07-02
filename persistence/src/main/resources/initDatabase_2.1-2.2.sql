/* SQL script to initialize of database for Czidlo in versions 2.1 or 2.2 */

/* Drop Indexes */

DROP INDEX IF EXISTS DIGITALDOCUMENT_ENTITYID;
DROP INDEX IF EXISTS DIGITALINSTANCE_DIGDOCID;
DROP INDEX IF EXISTS LIBRARY_REGISTRARID;
DROP INDEX IF EXISTS IEIDENTIFIER_VALUE;
DROP INDEX IF EXISTS URNNBN_REGISTRARCODEDOCUMENTCODE;
DROP INDEX IF EXISTS URNNBNRESERVED_REGISTRARCODEDOCUMENTCODE;



/* Drop Tables */

DROP TABLE IF EXISTS DDIDENTIFIER;
DROP TABLE IF EXISTS DIGITALINSTANCE;
DROP TABLE IF EXISTS URNNBN;
DROP TABLE IF EXISTS DIGITALDOCUMENT;
DROP TABLE IF EXISTS CATALOGUE;
DROP TABLE IF EXISTS DIGITALLIBRARY;
DROP TABLE IF EXISTS URNNBNGENERATOR;
DROP TABLE IF EXISTS URNNBNRESERVED;
DROP TABLE IF EXISTS USER_REGISTRAR;
DROP TABLE IF EXISTS REGISTRAR;
DROP TABLE IF EXISTS ARCHIVER;
DROP TABLE IF EXISTS IEIDENTIFIER;
DROP TABLE IF EXISTS ORIGINATOR;
DROP TABLE IF EXISTS PUBLICATION;
DROP TABLE IF EXISTS SOURCEDOCUMENT;
DROP TABLE IF EXISTS INTELECTUALENTITY;
DROP TABLE IF EXISTS USERACCOUNT;



/* Drop Sequences */

DROP SEQUENCE IF EXISTS SEQ_ARCHIVER;
DROP SEQUENCE IF EXISTS SEQ_CATALOGUE;
DROP SEQUENCE IF EXISTS SEQ_DIGITALDOCUMENT;
DROP SEQUENCE IF EXISTS SEQ_DIGITALINSTANCE;
DROP SEQUENCE IF EXISTS SEQ_DIGITALLIBRARY;
DROP SEQUENCE IF EXISTS SEQ_INTELECTUALENTITY;
DROP SEQUENCE IF EXISTS SEQ_USERACCOUNT;




/* Create Sequences */

CREATE SEQUENCE SEQ_ARCHIVER;
CREATE SEQUENCE SEQ_CATALOGUE;
CREATE SEQUENCE SEQ_DIGITALDOCUMENT;
CREATE SEQUENCE SEQ_DIGITALINSTANCE;
CREATE SEQUENCE SEQ_DIGITALLIBRARY;
CREATE SEQUENCE SEQ_INTELECTUALENTITY;
CREATE SEQUENCE SEQ_USERACCOUNT;



/* Create Tables */

CREATE TABLE ARCHIVER
(
	ID NUMERIC NOT NULL,
	CREATED TIMESTAMP NOT NULL,
	MODIFIED TIMESTAMP NOT NULL,
	NAME VARCHAR NOT NULL,
	DESCRIPTION VARCHAR,
	PRIMARY KEY (ID)
) WITHOUT OIDS;


CREATE TABLE CATALOGUE
(
	ID NUMERIC NOT NULL,
	REGISTRARID NUMERIC NOT NULL,
	CREATED TIMESTAMP NOT NULL,
	MODIFIED TIMESTAMP NOT NULL,
	NAME VARCHAR NOT NULL,
	DESCRIPTION VARCHAR,
	URLPREFIX VARCHAR NOT NULL,
	PRIMARY KEY (ID)
) WITHOUT OIDS;


CREATE TABLE DDIDENTIFIER
(
	REGISTRARID NUMERIC NOT NULL,
	DIGITALDOCUMENTID NUMERIC NOT NULL,
	TYPE VARCHAR NOT NULL,
	CREATED TIMESTAMP NOT NULL,
	MODIFIED TIMESTAMP NOT NULL,
	IDVALUE VARCHAR NOT NULL,
	PRIMARY KEY (REGISTRARID, DIGITALDOCUMENTID, TYPE),
	CONSTRAINT registrar_idType_idValue UNIQUE (REGISTRARID, TYPE, IDVALUE)
) WITHOUT OIDS;


CREATE TABLE DIGITALDOCUMENT
(
	ID NUMERIC NOT NULL,
	INTELECTUALENTITYID NUMERIC NOT NULL,
	REGISTRARID NUMERIC NOT NULL,
	ARCHIVERID NUMERIC NOT NULL,
	CREATED TIMESTAMP NOT NULL,
	MODIFIED TIMESTAMP NOT NULL,
	FINANCEDFROM VARCHAR,
	CONTRACTNUMBER VARCHAR,
	FORMAT VARCHAR,
	FORMATVERSION VARCHAR,
	EXTENT VARCHAR,
	RESOLUTIONHORIZONTAL INT,
	RESOLUTIONVERTICAL INT,
	COMPRESSION VARCHAR,
	COMPRESSIONRATIO FLOAT,
	COLORMODEL VARCHAR,
	COLORDEPTH INT,
	ICCPROFILE VARCHAR,
	PICTUREWIDTH INT,
	PICTUREHEIGHT INT,
	PRIMARY KEY (ID)
) WITHOUT OIDS;


CREATE TABLE DIGITALINSTANCE
(
	ID NUMERIC NOT NULL,
	DIGITALDOCUMENTID NUMERIC NOT NULL,
	DIGITALLIBRARYID NUMERIC NOT NULL,
	CREATED TIMESTAMP NOT NULL,
	MODIFIED TIMESTAMP NOT NULL,
	ACTIVE BOOLEAN NOT NULL,
	URL VARCHAR NOT NULL,
	FORMAT VARCHAR,
	ACCESSIBILITY VARCHAR,
	PRIMARY KEY (ID)
) WITHOUT OIDS;


CREATE TABLE DIGITALLIBRARY
(
	ID NUMERIC NOT NULL,
	REGISTRARID NUMERIC NOT NULL,
	CREATED TIMESTAMP NOT NULL,
	MODIFIED TIMESTAMP NOT NULL,
	NAME VARCHAR NOT NULL,
	DESCRIPTION VARCHAR,
	URL VARCHAR,
	PRIMARY KEY (ID)
) WITHOUT OIDS;


CREATE TABLE IEIDENTIFIER
(
	INTELECTUALENTITYID NUMERIC NOT NULL,
	TYPE VARCHAR NOT NULL,
	IDVALUE VARCHAR NOT NULL,
	PRIMARY KEY (INTELECTUALENTITYID, TYPE)
) WITHOUT OIDS;


CREATE TABLE INTELECTUALENTITY
(
	ID NUMERIC NOT NULL,
	CREATED TIMESTAMP NOT NULL,
	MODIFIED TIMESTAMP NOT NULL,
	ENTITYTYPE VARCHAR NOT NULL,
	DOCUMENTTYPE VARCHAR,
	DIGITALBORN BOOLEAN NOT NULL,
	OTHERORIGINATOR VARCHAR,
	DEGREEAWARDINGINSTITUTION VARCHAR,
	PRIMARY KEY (ID)
) WITHOUT OIDS;


CREATE TABLE ORIGINATOR
(
	INTELECTUALENTITYID NUMERIC NOT NULL,
	ORIGINTYPE VARCHAR NOT NULL,
	ORIGINVALUE VARCHAR NOT NULL,
	PRIMARY KEY (INTELECTUALENTITYID)
) WITHOUT OIDS;


CREATE TABLE PUBLICATION
(
	INTELECTUALENTITYID NUMERIC NOT NULL,
	PYEAR DECIMAL(4),
	PLACE VARCHAR,
	PUBLISHER VARCHAR,
	PRIMARY KEY (INTELECTUALENTITYID)
) WITHOUT OIDS;


CREATE TABLE REGISTRAR
(
	ID NUMERIC NOT NULL,
	CODE VARCHAR NOT NULL UNIQUE,
	ALLOWEDTOREGISTERFREEURNNBN BOOLEAN NOT NULL,
	PRIMARY KEY (ID)
) WITHOUT OIDS;


CREATE TABLE SOURCEDOCUMENT
(
	INTELECTUALENTITYID NUMERIC NOT NULL,
	TITLE VARCHAR,
	VOLUMETITLE VARCHAR,
	ISSUETITLE VARCHAR,
	CCNB VARCHAR,
	ISBN VARCHAR,
	ISSN VARCHAR,
	OTHERID VARCHAR,
	PUBLISHER VARCHAR,
	PUBLICATIONPLACE VARCHAR,
	PUBLICATIONYEAR NUMERIC(4),
	PRIMARY KEY (INTELECTUALENTITYID)
) WITHOUT OIDS;


CREATE TABLE URNNBN
(
	DIGITALDOCUMENTID NUMERIC NOT NULL,
	CREATED TIMESTAMP NOT NULL,
	MODIFIED TIMESTAMP NOT NULL,
	REGISTRARCODE VARCHAR NOT NULL,
	DOCUMENTCODE VARCHAR NOT NULL,
	PRIMARY KEY (DIGITALDOCUMENTID),
	CONSTRAINT registrarCodeDocumentCode UNIQUE (REGISTRARCODE, DOCUMENTCODE)
) WITHOUT OIDS;


CREATE TABLE URNNBNGENERATOR
(
	REGISTRARID NUMERIC NOT NULL,
	LASTDOCUMENTCODE VARCHAR DEFAULT '000000' NOT NULL,
	PRIMARY KEY (REGISTRARID)
) WITHOUT OIDS;


CREATE TABLE URNNBNRESERVED
(
	REGISTRARID NUMERIC NOT NULL,
	DOCUMENTCODE VARCHAR NOT NULL,
	CREATED TIMESTAMP NOT NULL,
	REGISTRARCODE VARCHAR NOT NULL,
	PRIMARY KEY (REGISTRARID, DOCUMENTCODE)
) WITHOUT OIDS;


CREATE TABLE USERACCOUNT
(
	ID NUMERIC NOT NULL,
	CREATED TIMESTAMP NOT NULL,
	MODIFIED TIMESTAMP NOT NULL,
	LOGIN VARCHAR NOT NULL UNIQUE,
	PASSWORD VARCHAR NOT NULL,
	ISADMIN BOOLEAN DEFAULT 'false' NOT NULL,
	EMAIL VARCHAR NOT NULL,
	PRIMARY KEY (ID)
) WITHOUT OIDS;


CREATE TABLE USER_REGISTRAR
(
	REGISTRARID NUMERIC NOT NULL,
	USERACCOUNTID NUMERIC NOT NULL,
	PRIMARY KEY (REGISTRARID, USERACCOUNTID)
) WITHOUT OIDS;



/* Create Foreign Keys */

ALTER TABLE DIGITALDOCUMENT
	ADD FOREIGN KEY (ARCHIVERID)
	REFERENCES ARCHIVER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE REGISTRAR
	ADD FOREIGN KEY (ID)
	REFERENCES ARCHIVER (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE DDIDENTIFIER
	ADD FOREIGN KEY (DIGITALDOCUMENTID)
	REFERENCES DIGITALDOCUMENT (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE DIGITALINSTANCE
	ADD FOREIGN KEY (DIGITALDOCUMENTID)
	REFERENCES DIGITALDOCUMENT (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE URNNBN
	ADD FOREIGN KEY (DIGITALDOCUMENTID)
	REFERENCES DIGITALDOCUMENT (ID)
	ON UPDATE RESTRICT
	ON DELETE SET NULL
;


ALTER TABLE DIGITALINSTANCE
	ADD FOREIGN KEY (DIGITALLIBRARYID)
	REFERENCES DIGITALLIBRARY (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE DIGITALDOCUMENT
	ADD FOREIGN KEY (INTELECTUALENTITYID)
	REFERENCES INTELECTUALENTITY (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE IEIDENTIFIER
	ADD FOREIGN KEY (INTELECTUALENTITYID)
	REFERENCES INTELECTUALENTITY (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE ORIGINATOR
	ADD FOREIGN KEY (INTELECTUALENTITYID)
	REFERENCES INTELECTUALENTITY (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE PUBLICATION
	ADD FOREIGN KEY (INTELECTUALENTITYID)
	REFERENCES INTELECTUALENTITY (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE SOURCEDOCUMENT
	ADD FOREIGN KEY (INTELECTUALENTITYID)
	REFERENCES INTELECTUALENTITY (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE CATALOGUE
	ADD FOREIGN KEY (REGISTRARID)
	REFERENCES REGISTRAR (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE DDIDENTIFIER
	ADD FOREIGN KEY (REGISTRARID)
	REFERENCES REGISTRAR (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE DIGITALDOCUMENT
	ADD FOREIGN KEY (REGISTRARID)
	REFERENCES REGISTRAR (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE DIGITALLIBRARY
	ADD FOREIGN KEY (REGISTRARID)
	REFERENCES REGISTRAR (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE URNNBNGENERATOR
	ADD FOREIGN KEY (REGISTRARID)
	REFERENCES REGISTRAR (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE URNNBNRESERVED
	ADD FOREIGN KEY (REGISTRARID)
	REFERENCES REGISTRAR (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE USER_REGISTRAR
	ADD FOREIGN KEY (REGISTRARID)
	REFERENCES REGISTRAR (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE USER_REGISTRAR
	ADD FOREIGN KEY (USERACCOUNTID)
	REFERENCES USERACCOUNT (ID)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;



/* Create Indexes */

CREATE INDEX DIGITALDOCUMENT_ENTITYID ON DIGITALDOCUMENT USING BTREE (INTELECTUALENTITYID);
CREATE INDEX DIGITALINSTANCE_DIGDOCID ON DIGITALINSTANCE (DIGITALDOCUMENTID);
CREATE INDEX LIBRARY_REGISTRARID ON DIGITALLIBRARY (REGISTRARID);
CREATE INDEX IEIDENTIFIER_VALUE ON IEIDENTIFIER USING BTREE (IDVALUE);
CREATE INDEX URNNBN_REGISTRARCODEDOCUMENTCODE ON URNNBN USING BTREE (REGISTRARCODE, DOCUMENTCODE);
CREATE INDEX URNNBNRESERVED_REGISTRARCODEDOCUMENTCODE ON URNNBNRESERVED (REGISTRARCODE, DOCUMENTCODE);

/* Create initial administrator account */
INSERT INTO USERACCOUNT(ID, CREATED, MODIFIED, LOGIN, PASSWORD, ISADMIN, EMAIL) 
VALUES(nextval('SEQ_USERACCOUNT'), LOCALTIMESTAMP, LOCALTIMESTAMP, 'admin', 'adminPassword', true, 'somone@somewhere.com');