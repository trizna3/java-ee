package src;

public class VravCommunicationUtil {

	public static final boolean LOG_ENABLED = false;

	public static final int HEADER_LOGON = 1;
	public static final int HEADER_LOGOFF = 2;
	public static final int HEADER_MESSAGE = 3;
	public static final int HEADER_ADD_TEXT = 4;
	public static final int HEADER_REMOVE_TEXT = 5;
	public static final int HEADER_REPLACE_TEXT = 6;
	
	
	public static void log(String message) {
		if (LOG_ENABLED) {
			System.out.println(message);
		} 
	}
	
	public static String generateName(int descriptor) {
		return "User " + descriptor;
	}
	
	public static String createServiceRequest(VravHeader header, String message) {
		if (message == null) {
			return null;
		}
		
		StringBuilder request = prepareServiceTransport(header);
		request.append(message);
		
		return request.toString();		
	}
	
	public static String createServiceResponse(int descriptor, VravHeader header, String message) {
		if (message == null) {
			return null;
		}
		
		StringBuilder response = prepareServiceTransport(header);
		response.append(descriptor);
		response.append(";");
		response.append(message);
		
		return response.toString();		
	}
	
	private static StringBuilder prepareServiceTransport(VravHeader header) {
		StringBuilder transport = new StringBuilder("Header=");
		
		transport.append(translateHeader(header));
		transport.append(";");
		return transport;
	}
	
	public static VravResponse parseServiceResponse(String response) {
		if (response == null || response.length() < 1) {
			return null;
		}
		
		int lowerBound = 7;
		int upperBound = response.indexOf(";");
		String headerCode = response.substring(lowerBound, upperBound);
		lowerBound = upperBound+1;
		upperBound = response.indexOf(";",lowerBound);
		String clientDescriptor = response.substring(lowerBound, upperBound);
		lowerBound = upperBound+1;
		String message = response.substring(lowerBound);
		
//		log("Response: Header code = " + headerCode + "Client descriptor = " + clientDescriptor + "Message = " + message);
		
		return new VravResponse(translateHeader(Integer.valueOf(headerCode)),Integer.valueOf(clientDescriptor),message);
	} 
	
	public static VravRequest parseServiceRequest(String request) {
		if (request == null || request.length() < 1) {
			return null;
		}
		
		int lowerBound = 7;
		int upperBound = request.indexOf(";");
		String headerCode = request.substring(lowerBound, upperBound);
		lowerBound = upperBound+1;
		String message = request.substring(lowerBound);
		
//		log("Response: Header code = " + headerCode + "Message = " + message);
		
		return new VravRequest(translateHeader(Integer.valueOf(headerCode)),message);
	}
	
	private static int translateHeader(VravHeader header) {
		switch (header) {
			case HEADER_LOGON:
				return HEADER_LOGON;
			case HEADER_LOGOFF:
				return HEADER_LOGOFF;
			case HEADER_MESSAGE:
				return HEADER_MESSAGE;
			case HEADER_ADD_TEXT:
				return HEADER_ADD_TEXT;
			case HEADER_REMOVE_TEXT:
				return HEADER_REMOVE_TEXT;
			case HEADER_REPLACE_TEXT:
				return HEADER_REPLACE_TEXT;
		}
		throw new IllegalArgumentException("Unknown header!");
	}
	
	private static VravHeader translateHeader(int headerCode) {
		switch (headerCode) {
			case HEADER_LOGON:
				return VravHeader.HEADER_LOGON;
			case HEADER_LOGOFF:
				return VravHeader.HEADER_LOGOFF;
			case HEADER_MESSAGE:
				return VravHeader.HEADER_MESSAGE;
			case HEADER_ADD_TEXT:
				return VravHeader.HEADER_ADD_TEXT;
			case HEADER_REMOVE_TEXT:
				return VravHeader.HEADER_REMOVE_TEXT;
			case HEADER_REPLACE_TEXT:
				return VravHeader.HEADER_REPLACE_TEXT;
		}
		throw new IllegalArgumentException("Unknown header!");
	}
	
	public static String createTextTransportRaw(VravTextTransport textTransport) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(textTransport.getFrom());
		sb.append(";");
		sb.append(textTransport.getTo());
		sb.append(";");
		sb.append(textTransport.getText());		
		
		return sb.toString();
	}
	
	public static VravTextTransport parseTextTransportRaw(VravHeader header, String text) {
		int lowerBound = 0;
		int upperBound = text.indexOf(";");
		
		String fromIdx = text.substring(lowerBound, upperBound);
		lowerBound = upperBound+1;
		upperBound = text.indexOf(";",lowerBound);
		String toIdx = text.substring(lowerBound, upperBound);
		lowerBound = upperBound+1;
		String message = text.substring(lowerBound);
		
		return new VravTextTransport(header, Integer.valueOf(fromIdx), Integer.valueOf(toIdx), message);
	}
}
