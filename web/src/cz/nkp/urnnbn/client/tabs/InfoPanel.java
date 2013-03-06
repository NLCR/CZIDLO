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
				""
						+ "<h2>URN:NBN Resolver</h2>"
						+ "<p>"
						+ "URN:NBN Resolver je aplikace, která umožňuje přidělení, evidenci, správu a vyhledávání trvalých identifikátorů URN:NBN "
						+ "pro digitální (digitalizované/e-born) dokumenty. Resolver udržuje informace přidružené k těmto identifikátorům, zejména základní bibliografické a technické údaje. "
						+ "Aplikace dostupná na <a href=\"http://resolver.nkp.cz\">resolver.nkp.cz</a> je určena pouze pro identifikátory URN:NBN vzniklé v českém prostředí (tedy začínajících prefixem <b>urn:nbn:cz</b>). "
						+ "Resolver je primárně pouze zprostředkující služba, digitální dokumenty nezpřístupňuje přímo, ale poskytuje URL odkazy na vlastní dokumenty v digitálních knihovnách."
						+ "Autoritou pro přidělování URN:NBN:CZ je Národní knihovna ČR, která přiřazuje tyto identifikátory s využitím aplikace.</p>"
						+ "<p>"
						+ "Resolver URN:NBN je v současnosti v pilotním provozu."
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
						+ "<p><h3>vývoj, správa aplikace</h3> Martin Řehánek <br/>rehan@mzk.cz</div>"
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
