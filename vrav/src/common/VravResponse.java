package src.common;

public class VravResponse {

	private VravHeader header;
	private int clientDescriptor;
	private String message;
	
	public VravResponse(VravHeader header, int clientDescriptor, String message) {
		this.setHeader(header);
		this.setClientDescriptor(clientDescriptor);
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


	public int getClientDescriptor() {
		return clientDescriptor;
	}


	public void setClientDescriptor(int clientDescriptor) {
		this.clientDescriptor = clientDescriptor;
	}
}
