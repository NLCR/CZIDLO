-- Description: Insert sample data into urnnbn_resolvation table for testing purposes
-- Usage: psql -d czidlo_core -U czidlo -v ON_ERROR_STOP=1 -f init_table_urnnbn_resolvation_insert_sample_resolving_data.sql

INSERT INTO urnnbn_resolvation ("id", registrarcode, documentcode, resolved)
VALUES (2259946,'aba007','0009a3','2026-01-01 12:13:14'),
       (2259947,'anl002','0006ac','2026-01-02 13:14:15'),
       (2259948,'aba001','0007l0','2026-01-03 14:15:16'),
       (2259949,'aba001','0003sf','2026-01-04 15:16:17'),
       (2259950,'aba007','0005j2','2026-01-05 16:17:18'),
       (2259951,'mzk','00061d','2026-01-06 17:18:19'),
       (2259952,'p01nk','0000hc','2026-01-07 18:19:20'),
       (2259953,'aba001','000332','2026-01-08 19:20:21'),
       (2259954,'aba001','0003sf','2026-01-09 20:21:22'),
       (1129975,'aba007','0009a3','2026-01-26 15:39:12.077'),
       (1129973,'pna001','00cy37','2026-01-25 23:06:02.001');


-- Normalize the sequence value for future inserts
SELECT setval(
    pg_get_serial_sequence('urnnbn_resolvation', 'id'),
    (SELECT COALESCE(MAX(id), 1) FROM urnnbn_resolvation),
    true
);