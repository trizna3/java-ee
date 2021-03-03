package dom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class VypisDovoleniek {

	private static final String OUTPUT_XML = "dovolenky2.xml";

	public static void main(String[] args) {
		if (args.length < 1)
			System.out.println("usage: VypisDovoleniek dovlenky.xml\n");
		else {
			try {
				Document doc = readDocument(args[0]);
				List<Dovolenka> dovolenky = parseDovolenky(doc);
				printDovolenky(dovolenky);
				
				Document newDoc = vytvorDocument(dovolenky);
				writeNewXml(newDoc, OUTPUT_XML);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static Document readDocument(String filename) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		builder.setErrorHandler(new VypisChyb());

		return builder.parse(filename);
	}

	private static List<Dovolenka> parseDovolenky(Document doc) {
		List<Dovolenka> dovolenky = new ArrayList<Dovolenka>();
		NodeList xmlDovolenky = doc.getElementsByTagName(Dovolenka.ELEMENT_NAME);
		for (int i = 0; i < xmlDovolenky.getLength(); i++) {
			Node nd = xmlDovolenky.item(i);

			if (nd != null) {
				dovolenky.add(new Dovolenka(nd));
			}
		}

		return dovolenky;
	}

	private static void writeNewXml(Document doc, String filename) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer xmlWriter = tf.newTransformer();
		xmlWriter.setOutputProperty(OutputKeys.INDENT, "yes");
		xmlWriter.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		xmlWriter.setOutputProperty(OutputKeys.ENCODING, "windows-1250");

		xmlWriter.transform(new DOMSource(doc), new StreamResult(new File(filename)));
	}

	private static void printDovolenky(List<Dovolenka> dovolenky) {
		for (Dovolenka dov : dovolenky) {
			System.out.println(dov.getRok() + ": " + dov.getKrajina() + ", " + dov.getPocetDni());
		}
	}

	private static Document vytvorDocument(List<Dovolenka> dovolenky) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();
		Document doc = impl.createDocument(null, "zoznamDovoleniek", null);
		
		Node zoznamDovoleniek = doc.getDocumentElement();
		
		for (Dovolenka dov : dovolenky) {
			Element xmlDovolenka = doc.createElement("dovolenka");
			xmlDovolenka.setAttribute("id", String.valueOf(dov.getId()));
			
			Element xmlMiesto = doc.createElement("miesto");
			xmlMiesto.setAttribute("krajina", dov.getKrajina());
			xmlMiesto.appendChild(doc.createTextNode(dov.getMiesto()));
			
			Element xmlRok = doc.createElement("rok");
			xmlRok.appendChild(doc.createTextNode(String.valueOf(dov.getRok())));
			
			Element xmlPocetDni = doc.createElement("pocetDni");
			xmlPocetDni.appendChild(doc.createTextNode(String.valueOf(dov.getPocetDni())));
			
			xmlDovolenka.appendChild(xmlMiesto);
			xmlDovolenka.appendChild(xmlRok);
			xmlDovolenka.appendChild(xmlPocetDni);
			
			zoznamDovoleniek.appendChild(xmlDovolenka);
		}

		return doc;
	}
}
