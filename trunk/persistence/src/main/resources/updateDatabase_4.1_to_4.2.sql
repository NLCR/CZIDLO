
/* salt for password hash */
ALTER TABLE USERACCOUNT ADD COLUMN PASSSALT VARCHAR NOT NULL default 'TODO: fix by database upgrader';
ALTER TABLE USERACCOUNT RENAME COLUMN PASSWORD TO PASSHASH;

/* IE-TITLE-SEARCH STUFF */

CREATE TABLE IE_TITLE (
   ID NUMERIC NOT NULL,
   TITLE VARCHAR
) WITHOUT OIDS;

CREATE OR REPLACE VIEW IE_TITLE_VIEW AS
SELECT
   title.id AS id,
   COALESCE(title.value, '') || ' ' || COALESCE(subtitle.value, '') || ' ' || COALESCE(issue.value, '') || ' ' || COALESCE(volume.value, '') || ' ' || COALESCE(ccnb.value, '') || ' ' || COALESCE(isbn.value, '') || ' ' || COALESCE(issn.value, '') AS title
FROM
   (SELECT intelectualentityid AS id, idvalue AS value FROM ieidentifier WHERE type = 'TITLE') AS title FULL OUTER JOIN
   (SELECT intelectualentityid AS id, idvalue AS value FROM ieidentifier WHERE type = 'SUB_TITLE') AS subtitle ON title.id = subtitle.id FULL OUTER JOIN
   (SELECT intelectualentityid AS id, idvalue AS value FROM ieidentifier WHERE type = 'VOLUME_TITLE') AS volume ON title.id = volume.id FULL OUTER JOIN
   (SELECT intelectualentityid AS id, idvalue AS value FROM ieidentifier WHERE type = 'ISSUE_TITLE') AS issue ON title.id = issue.id FULL OUTER JOIN
   (SELECT intelectualentityid AS id, idvalue AS value FROM ieidentifier WHERE type = 'CCNB') AS ccnb ON title.id = ccnb.id FULL OUTER JOIN
   (SELECT intelectualentityid AS id, idvalue AS value FROM ieidentifier WHERE type = 'ISBN') AS isbn ON title.id = isbn.id FULL OUTER JOIN
   (SELECT intelectualentityid AS id, idvalue AS value FROM ieidentifier WHERE type = 'ISSN') AS issn ON title.id = issn.id
;

 CREATE LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ie_title_update(NUMERIC) RETURNS void AS $BODY$
BEGIN
   IF (EXISTS (SELECT id FROM IE_TITLE_VIEW WHERE id = $1)) THEN
      IF (EXISTS (SELECT id FROM ie_title WHERE id = $1)) THEN
         UPDATE ie_title SET title = (SELECT title FROM ie_title_view WHERE id = $1) WHERE id = $1;
      ELSE
         INSERT INTO ie_title (SELECT * FROM IE_TITLE_VIEW WHERE id = $1);
      END IF;
   ELSE
      DELETE FROM ie_title WHERE id = id;
   END IF;
END;
$BODY$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ie_title_update_trigger_function() RETURNS TRIGGER AS $BODY$
   BEGIN
      IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
         EXECUTE ie_title_update(NEW.intelectualentityid);
      ELSIF (TG_OP = 'DELETE') THEN
         EXECUTE ie_title_update(OLD.intelectualentityid);
      END IF;
      RETURN NULL;
   END;
$BODY$
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS ie_title_update_trigger ON ieidentifier;
CREATE TRIGGER ie_title_update_trigger AFTER INSERT OR UPDATE OR DELETE ON ieidentifier FOR EACH ROW EXECUTE PROCEDURE ie_title_update_trigger_function();

INSERT INTO ie_title SELECT * FROM ie_title_view;

CREATE INDEX ie_title_fulltext_idx ON ie_title USING gin(to_tsvector('simple', lower(title)));


