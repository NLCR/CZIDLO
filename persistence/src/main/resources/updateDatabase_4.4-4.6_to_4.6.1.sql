/*******************************************/
/*******************************************/
/**  updateDatabase_4.4-4.6_to_4.6.1.sql  **/
/*******************************************/
/*******************************************/


/**********************************************************************/
/* Fix bug in function update_urnnbn_assignment                       */
/* https://github.com/NLCR/CZIDLO/issues/166                          */
/**********************************************************************/

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

/* initial filling table search_rsi_preprocessed */
DELETE FROM urnnbn_assignment_statistics_preprocessed;
INSERT INTO urnnbn_assignment_statistics_preprocessed (SELECT * FROM urnnbn_assignment_statistics_view);