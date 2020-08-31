/*******************************************/
/*******************************************/
/**  updateDatabase_4.6.1_to_5.0.sql  **/
/*******************************************/
/*******************************************/


/**********************************************************************/
/* Normalize ISBN data                                                */
/* https://github.com/NLCR/CZIDLO/issues/215                          */
/**********************************************************************/

/* backup just in case */
CREATE TABLE ISBN_BAK
(
	IEID NUMERIC NOT NULL,
	ISBN VARCHAR NOT NULL,
	PRIMARY KEY (IEID)
) WITHOUT OIDS;

INSERT INTO ISBN_BAK (SELECT intelectualentityid AS ieid, idvalue AS isbn FROM ieidentifier WHERE type='ISBN');

/* normalize data */
UPDATE ieidentifier SET idvalue = replace(idvalue, '-', '') WHERE type='ISBN';
UPDATE ieidentifier SET idvalue = replace(idvalue, '-', '') WHERE type='ISBN';

