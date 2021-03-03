package parser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import elements.OSMBounds;
import elements.OSMMap;
import elements.OSMNode;
import elements.OSMWay;

public class Parser {
	
	private final static Parser INSTANCE = new Parser();
	
	public static String MAP_XSD_FILE = "map.osm";
	
	public static Parser getInstance() {
		return INSTANCE;
	}
	
	public OSMMap parseMap() {
		Document doc = parseXml();
		if (doc == null) {
			throw new IllegalStateException("Error parsing map from xml");
		}
		
		List<OSMNode> osmNodes = parseAllOsmNodes(doc);
		List<OSMWay> osmWays = parseAllOsmWays(doc);
		OSMBounds bounds = parseOsmBounds(doc);
		
		return new OSMMap(bounds.getMinLon(),bounds.getMinLat(),bounds.getMaxLon(),bounds.getMaxLat(),osmNodes,osmWays);
	}
	
	private Document parseXml() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			builder.setErrorHandler(new ParseErrorHandler());

			Document doc = builder.parse(MAP_XSD_FILE);
			
			return doc;
		} catch (Exception e) {
			System.err.print("Error parsing map from xml");
			e.printStackTrace();
		}
		return null;
	}
	
	private List<OSMNode> parseAllOsmNodes(Document doc) {
		List<OSMNode> osmNodes = new ArrayList<OSMNode>();
		NodeList allMapNodes = doc.getElementsByTagName(OSMNode.ELEMENT_NAME);
		for (int i = 0; i < allMapNodes.getLength(); i++) {
			Node nd = allMapNodes.item(i);
			
			if (nd != null) {
				osmNodes.add(new OSMNode(nd));
			}
		}
		return osmNodes;
	}
	
	private List<OSMWay> parseAllOsmWays(Document doc) {
		List<OSMWay> osmWays = new ArrayList<OSMWay>();
		NodeList allMapWays = doc.getElementsByTagName(OSMWay.ELEMENT_NAME);
		
		for (int i = 0; i < allMapWays.getLength(); i++) {
			Node nd = allMapWays.item(i);
			
			if (nd != null) {
				osmWays.add(new OSMWay(nd));
			}
		}
		return osmWays;
	}
	
	private OSMBounds parseOsmBounds(Document doc) {
		NodeList allMapWays = doc.getElementsByTagName(OSMBounds.ELEMENT_NAME);
		
		for (int i = 0; i < allMapWays.getLength(); i++) {
			Node nd = allMapWays.item(i);
			
			if (nd != null) {
				return new OSMBounds(nd);
			}
		}
		return null;
	}
}
