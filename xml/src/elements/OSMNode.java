package elements;

import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class OSMNode extends OSMElement {
	private double lat;
	private double lon;
	
	public static final String ELEMENT_NAME = "node";
	public static final String LAT = "lat";
	public static final String LON = "lon";
	
	public OSMNode(int id, double lat, double lon, int version, Date timestamp, int changeset, int uid, String user) {
		super(id,version,timestamp,changeset,uid,user);
		this.setLat(lat);
		this.setLon(lon);
	}
	
	public OSMNode(Node xmlNodeRaw) {
		super(xmlNodeRaw);
		try {
			Element nodeElement = (Element) xmlNodeRaw;
			setLat(Double.valueOf(nodeElement.getAttributeNode(LAT).getValue()));
			setLon(Double.valueOf(nodeElement.getAttributeNode(LON).getValue()));
		} catch (Exception e) {
			System.err.print("Error parsing OSMNode ");
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "OSMNode [id=" + getId() + ", lat=" + getLat() + ", lon=" + getLon() + ", version=" + getVersion() + ", timestamp=" + getTimestamp() + ", changeset=" + getChangeset() + ", uid=" + getUid() + ", user=" + getUser() + "]";
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}
}
