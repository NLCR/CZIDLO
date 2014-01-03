
/* salt for password hash */
ALTER TABLE USERACCOUNT ADD COLUMN PASSSALT VARCHAR NOT NULL default 'TODO: fix by database upgrader';
ALTER TABLE USERACCOUNT RENAME COLUMN PASSWORD TO PASSHASH;


/* TODO: pridat tabulku pro vyhledavani, index, trigger*/

