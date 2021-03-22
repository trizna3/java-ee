package src.server.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBContext {
	
	private static final String URL = "jdbc:mysql://kempelen.dai.fmph.uniba.sk/";
	private static final String DB = "ee";
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String USER = "ee";
	private static final String PASS = "pivo";
	
	private static Connection connection;
	
	public static Connection getConnection() {
		if (connection == null) {
			initializeConnection();
		}
		return connection;
	}
	
	private static void initializeConnection() {
		try {
			Class.forName(DRIVER).newInstance();
	        connection = DriverManager.getConnection(URL+DB, USER, PASS);
		} catch (Exception e) {
			System.out.println("Cannot initalize db connection");
			e.printStackTrace();
		}
	}
}
