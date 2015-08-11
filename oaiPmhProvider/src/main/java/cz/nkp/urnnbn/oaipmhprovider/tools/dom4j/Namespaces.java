/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.tools.dom4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.dom4j.DocumentHelper;
import org.dom4j.Namespace;

/**
 *
 * @author Martin Řehánek
 */
public class Namespaces {

//    public static final Namespace foxml = DocumentHelper.createNamespace("foxml", "info:fedora/fedora-system:def/foxml#");
//    public static final Namespace fedora = DocumentHelper.createNamespace("fedora", "http://www.fedora.info/definitions/1/0/types/");
//    public static final Namespace fedora_model = DocumentHelper.createNamespace("fedora-model", "info:fedora/fedora-system:def/model#");
//    public static final Namespace kramerius = DocumentHelper.createNamespace("kramerius", "http://www.nsdl.org/ontologies/relationships#");
//    public static final Namespace mods = DocumentHelper.createNamespace("mods", "http://www.loc.gov/mods/v3");
//    public static final Namespace xsi = DocumentHelper.createNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
//    public static final Namespace rdf = DocumentHelper.createNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
//    public static final Namespace dc = DocumentHelper.createNamespace("dc", "http://purl.org/dc/elements/1.1/");
//    public static final Namespace tei = DocumentHelper.createNamespace("tei", "http://www.tei-c.org/ns/1.0");
//    public static final Namespace oai = DocumentHelper.createNamespace("oai", "http://www.openarchives.org/OAI/2.0/");
//    public static final Namespace adm = DocumentHelper.createNamespace("adm", "http://www.qbizm.cz/kramerius-fedora/image-adm-description");
//    public static final Namespace marc21 = DocumentHelper.createNamespace("marc21", "http://www.loc.gov/MARC21/slim");
    public static final Namespace OAI_DC = DocumentHelper.createNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
    public static final Namespace CZIDLO = DocumentHelper.createNamespace("czidlo", cz.nkp.urnnbn.xml.commons.Namespaces.CZIDLO_NS);
    private static final Map<String, String> unmodifiablePrefixUriMap;
    private static final Map<String, Namespace> prefixNamespaceMap = new HashMap<String, Namespace>();

    static {
//        prefixNamespaceMap.put(foxml.getPrefix(), foxml);
//        prefixNamespaceMap.put(fedora.getPrefix(), fedora);
//        prefixNamespaceMap.put(fedora_model.getPrefix(), fedora_model);
//        prefixNamespaceMap.put(kramerius.getPrefix(), kramerius);
//        prefixNamespaceMap.put(mods.getPrefix(), mods);
//        prefixNamespaceMap.put(xsi.getPrefix(), xsi);
//        prefixNamespaceMap.put(rdf.getPrefix(), rdf);
//        prefixNamespaceMap.put(dc.getPrefix(), dc);
//        prefixNamespaceMap.put(tei.getPrefix(), tei);
//        prefixNamespaceMap.put(oai.getPrefix(), oai);
//        prefixNamespaceMap.put(adm.getPrefix(), adm);
//        prefixNamespaceMap.put(marc21.getPrefix(), marc21);
        prefixNamespaceMap.put(OAI_DC.getPrefix(), OAI_DC);
        prefixNamespaceMap.put(CZIDLO.getPrefix(), CZIDLO);
        unmodifiablePrefixUriMap = initPrefixUriMap(prefixNamespaceMap);
    }

    private static Map<String, String> initPrefixUriMap(Map<String, Namespace> prefixNamespaceMap) {
        Map<String, String> prefixUriMap = new HashMap<String, String>();
        for (Namespace ns : prefixNamespaceMap.values()) {
            prefixUriMap.put(ns.getPrefix(), ns.getURI());
        }
        return Collections.unmodifiableMap(prefixUriMap);
    }

    public static Map<String, String> getPrefixUriMap() {
        return unmodifiablePrefixUriMap;
    }

}
