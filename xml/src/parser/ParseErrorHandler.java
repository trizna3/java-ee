package parser;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ParseErrorHandler implements ErrorHandler {

	private String textHlaseni(SAXParseException e) {
		return e.getSystemId() + "\n" + "row: " + e.getLineNumber() + " col: " + e.getColumnNumber() + "\n" + e.getMessage();
	}

	public void warning(SAXParseException e) {
		System.out.println("Warning: " + textHlaseni(e));
	}

	public void error(SAXParseException e) throws SAXException {
		throw new SAXException("Erorr: " + textHlaseni(e));
	}

	public void fatalError(SAXParseException e) throws SAXException {
		throw new SAXException("Fatal error: " + textHlaseni(e));
	}
}