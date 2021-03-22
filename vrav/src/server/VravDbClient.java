package src.server;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import src.server.jdbc.DBContext;
import src.server.rdg.Activity;
import src.server.rdg.ActivityType;
import src.server.rdg.Actor;

public class VravDbClient {

	public static void storeActivity(String username, ActivityType activityType, boolean success) {
		Activity activity = new Activity();
		
		activity.setActorName(username);
		activity.setTimestamp(new Timestamp(new Date().getTime()));
		activity.setActivityType(activityType);
		activity.setSuccess(Boolean.valueOf(success));
		
		activity.store();
	}
	
	public static void createTables() {
		try {
		PreparedStatement stmt = null;
		
		// create actor table
		
		stmt= DBContext.getConnection().prepareStatement("DROP TABLE IF EXISTS "+Actor.TABLENAME+" CASCADE;");
		stmt.executeUpdate();
		stmt= DBContext.getConnection().prepareStatement(Actor.getCreateStatement());
		stmt.executeUpdate();
		
		// create activity table
		stmt= DBContext.getConnection().prepareStatement("DROP TABLE IF EXISTS "+Activity.TABLENAME+" CASCADE;");
		stmt.executeUpdate();
		stmt = DBContext.getConnection().prepareStatement(Activity.getCreateStatement());
		stmt.executeUpdate();

		// prefill actor table
		DBContext.getConnection().createStatement().executeUpdate(Actor.getPrefillStatement());
		
		} catch (SQLException e) {
			System.out.println("Error creating db tables");
			e.printStackTrace();
		}
	}
}
