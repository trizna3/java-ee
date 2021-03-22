package src.server.rdg;

public enum ActivityType {
	
	LOGON,
	LOGOFF,
	SEND_MESSAGE;
	
	public static void validate(ActivityType value) {
		for (ActivityType type : values()) {
			if (type == value) {return;}
		}
		new IllegalArgumentException("Invalid ActivityType enum value " + value).printStackTrace();
	}
}
