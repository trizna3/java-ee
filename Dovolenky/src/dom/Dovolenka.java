package dom;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Dovolenka {

//	<dovolenka id="1">
//		<miesto krajina="Slovensko">Chopok</miesto>
//		<rok>1994</rok>
//		<pocetDni>12</pocetDni>
//	</dovolenka>
	
	private int id;
	private String krajina;
	private String miesto;
	private int rok;
	private int pocetDni;
	
	public static final String ELEMENT_NAME = "dovolenka";
	public static final String ID = "id";
	public static final String KRAJINA = "krajina";
	public static final String MIESTO = "miesto";
	public static final String ROK = "rok";
	public static final String POCET_DNI = "pocetDni";
	
	public Dovolenka(Node xmlDovolenka) {
		try {
			Element e = (Element) xmlDovolenka;
			setId(Integer.valueOf(e.getAttributeNode(ID).getValue()));
			NodeList children = e.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (MIESTO.equals(child.getNodeName())) {
					setKrajina(((Element)child).getAttributeNode(KRAJINA).getValue());
					setMiesto(child.getFirstChild().getTextContent());
				} else if (ROK.equals(child.getNodeName())) {
					setRok(Integer.valueOf(child.getFirstChild().getTextContent()));
				} else if (POCET_DNI.equals(child.getNodeName())) {
					setPocetDni(Integer.valueOf(child.getFirstChild().getTextContent()));
				}
			}
		} catch (Exception e) {
			System.err.print("Error parsing OSMNode ");
			e.printStackTrace();
		}
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKrajina() {
		return krajina;
	}

	public void setKrajina(String krajina) {
		this.krajina = krajina;
	}

	public String getMiesto() {
		return miesto;
	}

	public void setMiesto(String miesto) {
		this.miesto = miesto;
	}

	public int getRok() {
		return rok;
	}

	public void setRok(int rok) {
		this.rok = rok;
	}

	public int getPocetDni() {
		return pocetDni;
	}

	public void setPocetDni(int pocetDni) {
		this.pocetDni = pocetDni;
	}
}
