import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

public class VypisDovoleniek {
	public static void main(String[] args) {
		if (args.length < 1)
			System.out.println("usage: VypisDovoleniek dovlenky.xml\n");
		else
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				spf.setValidating(false);
				SAXParser p = spf.newSAXParser();
				XMLReader parser = p.getXMLReader();
				parser.setErrorHandler(new VypisChyb());
				ParserDovoleniek d = new ParserDovoleniek();
				parser.setContentHandler(d);
				parser.parse(args[0]);
				d.vypisDovolenky();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
