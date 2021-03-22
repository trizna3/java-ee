package src.common;

public class VravRequest { 
	private VravHeader header;
	private String message;
	
	
	public VravRequest(VravHeader header, String message) {
		this.setHeader(header);
		this.setMessage(message);
	}


	public VravHeader getHeader() {
		return header;
	}


	public void setHeader(VravHeader header) {
		this.header = header;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
}
