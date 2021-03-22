package src.server.rdg;

public enum UserLevel {

	ADMIN,
	BASIC;
	
	public static void validate(UserLevel value) {
		for (UserLevel level : values()) {
			if (level == value) {return;}
		}
		new IllegalArgumentException("Invalid UserLevel enum value " + value).printStackTrace();
	}
}
