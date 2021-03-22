package src.server.rdg;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import src.server.jdbc.DBContext;

public class ActivityDAO {

	public static List<Activity> getAllActivities() throws SQLException {
		List<Activity> result = new ArrayList<Activity>();
		try (PreparedStatement s = DBContext.getConnection().prepareStatement("SELECT * FROM "+Activity.TABLENAME+";")) {

            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                	Activity activity = new Activity();

                    activity.setId(r.getInt("id"));
                    activity.setActorName(r.getString("actorName"));
                    activity.setTimestamp(r.getTimestamp("timestamp"));
                    activity.setActivityType(ActivityType.valueOf(r.getString("activityType")));
                    activity.setSuccess(r.getBoolean("success"));
                    
                    result.add(activity);
                }
            }
        }
		return result;
	}
}
