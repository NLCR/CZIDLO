/*******************************************/
/*******************************************/
/**     updateDatabase_5.0_to_6.0.sql     **/
/*******************************************/
/*******************************************/

/***)**********************************************/
/*  Create indexes for more efficient ES indexer  */
/**************************************************/
CREATE INDEX IF NOT EXISTS registrarscopeid_digitaldocumentid_idx ON public.registrarscopeid (digitaldocumentid);
CREATE INDEX IF NOT EXISTS registrarscopeid_ddid_type_idx ON public.registrarscopeid (digitaldocumentid, type);
CREATE INDEX IF NOT EXISTS digitaldocument_modified_id_idx ON public.digitaldocument (modified, id);
CREATE INDEX IF NOT EXISTS urnnbn_resolvation_resolved_id_idx ON public.urnnbn_resolvation (resolved, id);
