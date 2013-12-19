DROP TABLE IE_TITLE;

CREATE TABLE IE_TITLE (
   ID NUMERIC NOT NULL,
   TITLE VARCHAR
) WITHOUT OIDS;

CREATE OR REPLACE VIEW IE_TITLE_VIEW AS
SELECT
   title.id AS id,
   COALESCE(title.value, '') || ' ' || COALESCE(subtitle.value, '') || ' ' || COALESCE(issue.value, '') || ' ' || COALESCE(volume.value, '') AS title
FROM
   (SELECT intelectualentityid AS id, idvalue AS value FROM ieidentifier WHERE type = 'TITLE') AS title FULL OUTER JOIN
   (SELECT intelectualentityid AS id, idvalue AS value FROM ieidentifier WHERE type = 'SUB_TITLE') AS subtitle ON title.id = subtitle.id FULL OUTER JOIN
   (SELECT intelectualentityid AS id, idvalue AS value FROM ieidentifier WHERE type = 'VOLUME_TITLE') AS volume ON title.id = volume.id FULL OUTER JOIN
   (SELECT intelectualentityid AS id, idvalue AS value FROM ieidentifier WHERE type = 'ISSUE_TITLE') AS issue ON title.id = issue.id
;

DROP FUNCTION ie_title_update(NUMERIC);
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

DROP TRIGGER ie_title_update_trigger ON ieidentifier;
CREATE TRIGGER ie_title_update_trigger AFTER INSERT OR UPDATE OR DELETE ON ieidentifier FOR EACH ROW EXECUTE PROCEDURE ie_title_update_trigger_function();

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

INSERT INTO ie_title SELECT * FROM ie_title_view;

CREATE INDEX ie_title_fulltext_idx ON ie_title USING gin(to_tsvector('simple', lower(title)));

