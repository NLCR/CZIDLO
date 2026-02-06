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
