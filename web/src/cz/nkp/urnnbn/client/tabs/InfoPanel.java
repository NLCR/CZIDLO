package cz.nkp.urnnbn.client.tabs;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class InfoPanel extends SingleTabContentPanel {
	public InfoPanel(TabsPanel tabsPanel) {
		super(tabsPanel);
		init();
	}

	private void init() {
		Widget content = new HTML(
						"<h2>CZIDLO</h2>"
						
						+ "<p>"
						+ "CZIDLO je softwarový nástroj pro podporu národního systému trvalé identifikace ČIDLO (Český systém pro identifikaci a lokalizaci dokumentů digitálního kulturního dědictví) založeného na standardu URN:NBN." 
						+ "</p>"
						+ "<p>"
						+ "Základní funkce nástroje jsou:"
						+ "<ol>"
						+ "<li>přidělování identifikátorů URN:NBN (automatizovaně prostřednictvím komunikačního aplikačního programového rozhraní nebo manuálně přes webové rozhraní), výměnou za metadata identifikovaných dokumentů,</li>"
						+ "<li>správa identifikátorů (např. jejich deaktivace) a k nim přidružených metadat,</li>"
						+ "<li>vkládání a dodatečná aktualizace adres URL digitálních dokumentů, kterým byl přidělen identifikátor URN:NBN (manuálně přes webové rozhraní/automatizovaně přes API/utilitou OAI Adapter),</li>"
						+ "<li>přesměrovávací služba (resolver) zajišťující přesměrovávání webového prohlížeče z URN:NBN na aktuální URL umístění dokumentu, případně na záznam dokumentu v CZIDLO,</li>"
						+ "<li>správa uživatelských účtů, práv a záznamů registrátorů (včetně digitálních knihoven a katalogů registrátorů) přes webové rozhraní,</li>"
						+ "<li>vyhledávání záznamů přes webové rozhraní,</li>"
						+ "<li>spouštění procesů na straně serveru přes webové rozhraní (OAI Adapter, export seznamu identifikátorů),</li>" 
						+ "<li>OAI-PMH rozhraní pro hromadné sklízení záznamů externími systémy.</li>"
						+ "</ol>"
						+ "</p>"
						+ "<p>"
						+ "Nástroj CZIDLO byl vyvinut v letech 2011-2012."
						+ "Softwarové řešení je založeno na opensource technologiích (Java, databáze PostgreSQL, Spring security, GWT aj.) a uznávaných standardech (XML, XSD, XSLT, HTTP, REST aj.). Nástroj je vydaný pod otevřenou licencí GNU GPL v3." 
						+ "Zdrojové kódy, instalační balíky, dokumentace apod. jsou veřejně dostupné na webu projektu."
						+ "CZIDLO tak podporuje zajištění trvalého přístupu k digitálním dokumentům navzdory změnám internetových adres a zajištění synchronizace mezi různými deriváty digitálního dokumentu v různých systémech (generování jedinečných identifikátorů) a umožňuje verifikovat citace (funkce udržování metadat o identifikovaných dokumentech)."
						+ "</p>"
						
						+ "<a href=\"http://code.google.com/p/urnnbn-resolver-v2/w/list\">Dokumentace</a>, "
						+ "<a href=\"http://code.google.com/p/urnnbn-resolver-v2/downloads/list\">soubory ke stažení</a> a "
						+ "<a href=\"http://code.google.com/p/urnnbn-resolver-v2/source/\">zdrojový kód aplikace</a> "
						+ "jsou dostupné na <a href=\"http://code.google.com/p/urnnbn-resolver-v2/\">webu projektu</a>."
						+ " Nalezené chyby v aplikaci prosím hlaste na <a href=\"http://code.google.com/p/urnnbn-resolver-v2/issues/list\">issue tracker</a>."
						+ "</p>"
						+ "<h2>Vyhledávání</h2>"
						+ " Aplikace umožňuje vyhledávat digitální dokumenty primárně podle identifikátoru URN:NBN, dále podle "
						+ "identifikátoru čČNB (číslo české národní bibliografie), čísel ISSN, ISBN (včetně jejich tištěné předlohy) a názvových údajů (např. titul monografie, název ročníku periodika).<p>"
						+ "URN:NBN (např.:&nbspurn:nbn:cz:aba001-00003t)<br>"
						+ "čČNB (např.:&nbsp;cnb001726942)<br>"
						+ "ISBN (např.:&nbsp;8090119964)<br>"
						+ "ISSN (např.:&nbsp;1803-4217)<br>" 
						+ "<h2>Kontakty</h2>"
						+ "<p><h3>odborný garant</h3> Ladislav Cubr <br/>Ladislav.Cubr@nkp.cz</p>"
						+ "<p><h3>kurátor</h3> Zdeněk Vašek <br/>Zdenek.Vasek@nkp.cz</p>"
						+ "<p><h3>vývoj, správa aplikace</h3> Martin Řehánek <br/>Martin.Rehanek@gmail.com</div>"
						+ "<p><h3>správa serveru</h3> Leoš Junek <br/>Leos.Junek@nkp.cz</p>");
		add(content);
	}

	@Override
	public void onSelection() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeselectionSelection() {
		// TODO Auto-generated method stub
		
	}
}
