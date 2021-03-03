import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ParserDovoleniek extends DefaultHandler {
	private StringBuffer rok = new StringBuffer(10);
	private StringBuffer miesto = new StringBuffer(50);
	private StringBuffer pocetDni = new StringBuffer(10);
	private String krajina;
	private boolean spracovavamRok, spracovavamMiesto,spracovavamPocetDni;
	private List<Dovolenka> dovolenky;
	
	private class Dovolenka {
		private String krajina;
		private int rok;
		private int pocetDni;
		
		public Dovolenka(String krajina, int rok, int pocetDni) {
			this.krajina = krajina;
			this.rok = rok;
			this.pocetDni = pocetDni;
		}

		public String getKrajina() {
			return krajina;
		}

		public int getRok() {
			return rok;
		}

		public int getPocetDni() {
			return pocetDni;
		}
		
		
	} 

	public void vypisDovolenky() {
		for (Dovolenka dov : dovolenky) {
			System.out.println(dov.getRok() + ": " + dov.getKrajina() + ", " + dov.getPocetDni());
		}
	}

	public void startDocument() {
		dovolenky = new ArrayList<ParserDovoleniek.Dovolenka>();
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) {
		if (qName.equals("rok") == true) {
			spracovavamRok = true;
			rok.setLength(0);
		} else if (qName.equals("miesto") == true) {
			spracovavamMiesto = true;
			krajina = atts.getValue("krajina");
			miesto.setLength(0);
		} else if (qName.equals("pocetDni") == true) {
			spracovavamPocetDni = true;
			pocetDni.setLength(0);
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (qName.equals("rok") == true) {
			spracovavamRok = false;
		} else if (qName.equals("miesto") == true) {
			spracovavamMiesto = false;
		} else if (qName.equals("pocetDni") == true) {
			spracovavamPocetDni = true;
		} else if (qName.equals("dovolenka") == true) {
			miesto.append(" (");
			miesto.append(krajina);
			miesto.append(")");

			dovolenky.add(new Dovolenka(miesto.toString(),Integer.parseInt(rok.toString().trim()),Integer.parseInt(pocetDni.toString().trim())));
		}
	}

	public void characters(char[] ch, int start, int length) {
		if (spracovavamRok == true) {
			rok.append(ch, start, length);
		} else if (spracovavamMiesto == true) {
			miesto.append(ch, start, length);
		} else if (spracovavamPocetDni) {
			pocetDni.append(ch, start, length);
		}
	}
}
