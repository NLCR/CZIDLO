/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.legacyrecordsimport;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Martin Řehánek
 */
public class SiglaLibraryIdMapping {

    private static final Map<String, Integer> mapping = new HashMap<String, Integer>();

    static {
        mapping.put("aba001", Integer.valueOf(1));
        mapping.put("aba006", Integer.valueOf(2));
        mapping.put("aba007", Integer.valueOf(3));
        mapping.put("aba008", Integer.valueOf(4));
        mapping.put("aba010", Integer.valueOf(5));
        mapping.put("abe045", Integer.valueOf(6));
        mapping.put("abc135", Integer.valueOf(7));
        mapping.put("abe190", Integer.valueOf(8));
        mapping.put("abe310", Integer.valueOf(9));
        mapping.put("abe323", Integer.valueOf(10));
        mapping.put("abe336", Integer.valueOf(11));
        mapping.put("abe343", Integer.valueOf(12));
        mapping.put("boa001", Integer.valueOf(13));
        mapping.put("bve301", Integer.valueOf(15));
        mapping.put("bve302", Integer.valueOf(16));
        mapping.put("cbe301", Integer.valueOf(17));
        mapping.put("ghe302", Integer.valueOf(18));
        mapping.put("hka001", Integer.valueOf(19));
        mapping.put("hke302", Integer.valueOf(20));
        mapping.put("jhe301", Integer.valueOf(21));
        mapping.put("kmg001", Integer.valueOf(22));
        mapping.put("ktg503", Integer.valueOf(23));
        mapping.put("kve303", Integer.valueOf(24));
        mapping.put("kvg001", Integer.valueOf(25));
        mapping.put("lia001", Integer.valueOf(26));
        mapping.put("lid001", Integer.valueOf(27));
        mapping.put("ola001", Integer.valueOf(28));
        mapping.put("osa001", Integer.valueOf(29));
        mapping.put("pae302", Integer.valueOf(30));
        mapping.put("pna001", Integer.valueOf(31));
        mapping.put("roe301", Integer.valueOf(32));
        mapping.put("ulg001", Integer.valueOf(33));
        mapping.put("zlg001", Integer.valueOf(34));
    }

    static int getLibraryId(String sigla) throws SiglaNotFoundException {
        String s = sigla.toLowerCase();
        if(!mapping.containsKey(s)) {
            throw new SiglaNotFoundException("EXC: " + "sigla " +  sigla + " not found");
        }
        return mapping.get(s);
    }
}
