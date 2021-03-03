package elements;

public class OSMTag {

	private String k;
	private String v;
	
	public OSMTag(String k, String v) {
		this.k = k;
		this.v = v;
	}

	public String getKey() {
		return k;
	}
	
	public void setKey(String key) {
		this.k = key;
	}
	
	public String getValue() {
		return v;
	}
	
	public void setValue(String value) {
		this.v = value;
	}
	
}