package elements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class OSMElement {
	private long id;
	private int version;
	private Date timestamp;
	private int changeset;
	private int uid;
	private String user;
	
	public static final String ID = "id";
	public static final String VERSION = "version";
	public static final String TIMESTAMP = "timestamp";
	public static final String CHANGESET = "changeset";
	public static final String UID = "uid";
	public static final String USER = "user";
	
	protected static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
	public OSMElement(long id, int version, Date timestamp, int changeset, int uid, String user) {
		this.setId(id);
		this.setVersion(version);
		this.setTimestamp(timestamp);
		this.setChangeset(changeset);
		this.setUid(uid);
		this.setUser(user);
	}
	
	public OSMElement(Node xmlNodeRaw) {
		try {
			Element nodeElement = (Element) xmlNodeRaw;
			
			setId(Long.valueOf(nodeElement.getAttributeNode(ID).getValue()));
			setVersion(Integer.valueOf(nodeElement.getAttributeNode(VERSION).getValue()));
			setTimestamp(new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.ENGLISH).parse(nodeElement.getAttributeNode(TIMESTAMP).getValue()));			
			setChangeset(Integer.valueOf(nodeElement.getAttributeNode(CHANGESET).getValue()));
			setUid(Integer.valueOf(nodeElement.getAttributeNode(UID).getValue()));
			setUser(String.valueOf(nodeElement.getAttributeNode(USER).getValue()));
		} catch (Exception e) {
			System.err.print("Error parsing " + this.getClass().toString());
			e.printStackTrace();
		}		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getChangeset() {
		return changeset;
	}

	public void setChangeset(int changeset) {
		this.changeset = changeset;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
