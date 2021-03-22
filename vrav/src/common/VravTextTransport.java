package src.common;

public class VravTextTransport {

	private VravHeader header;
	private int from;
	private int to;
	private String text;
	
	public VravTextTransport(VravHeader header, int from, int to, String text) {
		this.setHeader(header);
		this.setFrom(from);
		this.setTo(to);
		this.setText(text);
	}
	
	public VravHeader getHeader() {
		return header;
	}
	
	public void setHeader(VravHeader header) {
		this.header = header;
	}
	
	public int getFrom() {
		return from;
	}
	
	public void setFrom(int from) {
		this.from = from;
	}
	
	public int getTo() {
		return to;
	}
	
	public void setTo(int to) {
		this.to = to;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}	
}
