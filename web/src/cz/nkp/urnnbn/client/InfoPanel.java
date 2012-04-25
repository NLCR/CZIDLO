package cz.nkp.urnnbn.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class InfoPanel extends ScrollPanel {
	public InfoPanel() {
		super();
		init();
	}

	private void init() {
		Widget content = new HTML(
				"<h2>Upozornění</h2>"
						+ "Resolver URN:NBN je v současnosti v pilotním provozu na datech uložených v NK ČR.<br>"
						+ "URN:NBN resolver je aplikace, která umožňuje přidělení, evidenci, správu a vyhledávání trvalých identifikátorů URN:NBN "
						+ "pro digitální dokumenty. Resolver udržuje informace přidružené k těmto identifikátorům, zejména základní bibliografické "
						+ "a technické údaje. Aplikace URN:NBN resolver je určena pouze pro identifikátory URN:NBN vzniklé v českém prostředí "
						+ "(tedy začínajících prefixem <b>urn:nbn:cz</b>).<p>"
						+ "Autoritou pro přidělování URN:NBN:CZ je Národní knihovna ČR, která přiřazuje tyto identifikátory s využitím resolveru.<p>"
						+ "<h2>Vyhledávání</h2>"
						+ " Aplikace umožňuje vyhledávat digitální dokumenty primárně podle identifikátoru URN:NBN, dále podle "
						+ "identifikátoru čČNB (číslo české národní bibliografie)m čísel ISSN, ISBN (včetně jejich tištěné předlohy) a názvových údajů (např. titul monografie, název ročníku periodika).<p>"
						+ "<span style=\"color: black;\">URN:NBN</span> (např.:&nbsp;<span>urn:nbn:cz:aba000:0010vv</span>&nbsp;)<br>"
						+ "<span style=\"color: black;\">čČNB</span> (např.:&nbsp;<span>cnb001726942</span>&nbsp;)<br>"
						+ "<span style=\"color: black;\">ISBN</span> (např.:&nbsp;<span>80-7051-047-1</span>&nbsp;)<br>"
						+ "<span style=\"color: black;\">ISSN</span> (např.:&nbsp;<span>1803-4217</span>&nbsp;)<br><p>"
						+ "Aplikace vyhledává podle jakéhokoliv zadaného řetězce, počet zobrazených nalezených dokumentů je omezen na 500. K přesnému "
						+ "vyhledání konkrétního dokumentu je nutné zadat identifikátor v jeho úplném znění podle příkladů výše uvedených.<p>"
						+ "Resolver je primárně pouze zprostředkující služba, nezajišťuje zpřístupňování dokumentů. Poskytuje URL odkazy na vlastní "
						+ "dokumenty v digitálních knihovnách."
//						+ "<h2>Podmínky přidělění URN:NBN</h1>"
//						+ "URN:NBN může získat pouze digitální dokument, který je/bude uložen v digitálním repozitáři Národní knihovny ČR.<br>"
//						+ "V aktuální fázi přidělujeme URN:NBN pouze digitalizovaným dokumentům, tj. dokumentům, které vznikly digitalizací tištěné "
//						+ "předlohy (tištěné monografie, tištěné seriály).<br>"
//						+ "Archivace v repozitáři NK je povinná pro dokumenty digitalizované z financí rozpočtu NK ČR, dále pro dokumenty vzniklé v projektech "
//						+ "VISK7, Norské fondy a Povodně (hrazeno MK). Dokumenty z těchto projektů dostaly identifikátory URN:NBN.<p>"
//						+ "Pokud chce v této chvíli jiná instituce než NK ČR pro své digitální dokumenty identifikátor URN:NBN, musí uložit digitální kopii "
//						+ "identifikovaného dokumentu do repozitáře NK ČR. V dalších fázích vývoje se počítá s otevřením systému pro další instituce, "
//						+ "bez podmínky uložení dokumentů v NK ČR.<p>"
//						+ "Dokument označený identifikátorem URN:NBN musí odpovídat datovému modelu pro URN:NBN."
//						+ "<h2>Datový model</h2>"
//						+ "Pro datový model zavádíme dva základní pojmy: intelektuální entita a digitální reprezentace.<br>"
//						+ "Intelektuální entitou se zde rozumí tištěná předloha pro digitalizační aktivity.<br>"
//						+ "Digitální reprezentace je množina všech počítačových souborů, jejichž reprodukcí (například v internetovém prohlížeči) získáme "
//						+ "jednu intelektuální entitu a která je jako tento jeden celek (jako jedna intelektuální entita) archivována a zpřístupňována "
//						+ "uživatelům. Např. sto souborů ve formátu JPEG představujících digitalizovanou verzi tištěné knihy Máchův Máj.<br>"
//						+ "V našem pilotním projektu vznikly všechny digitální reprezentace digitalizací, tedy zatím všechny digitální reprezentace "
//						+ "jsou digitalizované dokumenty."
//						+ "<img src=\"img/data_model.jpg\"/>"
						);
		add(content);
	}
}
