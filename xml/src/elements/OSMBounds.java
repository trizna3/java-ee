package elements;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class OSMBounds {
	private double minLat;
	private double minLon;
	private double maxLat;
	private double maxLon;
	
	public static final String ELEMENT_NAME = "bounds";
	
	public static final String MIN_LAT = "minlat";
	public static final String MIN_LON = "minlon";
	public static final String MAX_LAT = "maxlat";
	public static final String MAX_LON = "maxlon";
	
	public OSMBounds(double minLat, double minLon, double maxLat, double maxLon) {
		this.setMinLat(minLat);
		this.setMinLon(minLon);
		this.setMaxLat(maxLat);
		this.setMaxLon(maxLon);
	}

	public OSMBounds(Node xmlBoundsRaw) {
		try {
			Element nodeElement = (Element) xmlBoundsRaw;
			
			setMinLat(Double.valueOf(nodeElement.getAttributeNode(MIN_LAT).getValue()));
			setMinLon(Double.valueOf(nodeElement.getAttributeNode(MIN_LON).getValue()));
			setMaxLat(Double.valueOf(nodeElement.getAttributeNode(MAX_LAT).getValue()));
			setMaxLon(Double.valueOf(nodeElement.getAttributeNode(MAX_LON).getValue()));
		} catch (Exception e) {
			System.err.print("Error parsing " + this.getClass().toString());
			e.printStackTrace();
		}
	}
	
	public double getMinLat() {
		return minLat;
	}

	public void setMinLat(double minLat) {
		this.minLat = minLat;
	}

	public double getMinLon() {
		return minLon;
	}

	public void setMinLon(double minLon) {
		this.minLon = minLon;
	}

	public double getMaxLat() {
		return maxLat;
	}

	public void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}

	public double getMaxLon() {
		return maxLon;
	}

	public void setMaxLon(double maxLon) {
		this.maxLon = maxLon;
	}
	
	
	
}
