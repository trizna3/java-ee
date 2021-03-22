package src.server.rdg;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import src.server.jdbc.DBContext;

public class Activity extends RdgObject {

	public static final String TABLENAME = "TAB_trizna_activity";
	
	private String actorName;
	private Timestamp timestamp;
	private ActivityType activityType;
	private Boolean success;
	
	public String getActorName() {
		return actorName;
	}
	public void setActorName(String actorName) {
		this.actorName = actorName;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public ActivityType getActivityType() {
		return activityType;
	}
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}
	
	@Override
	protected void update() throws SQLException {
		validateEnumValues();
		
		try (PreparedStatement s = DBContext.getConnection().prepareStatement("UPDATE " + getTablename() + " SET actorName = ?, timestamp = ?, activityType = ?, success = ? WHERE id = ?")) {
			s.setString(1, getActorName());
			s.setTimestamp(2, getTimestamp());
			s.setString(3, getActivityType().toString());
			s.setBoolean(4, getSuccess());
			s.setInt(5, getId());

			s.executeUpdate();
		}
	}

	@Override
	protected void insert() throws SQLException {
		validateEnumValues();
		
		try (PreparedStatement s = DBContext.getConnection().prepareStatement("INSERT INTO " + getTablename() + " (actorName,timestamp,activityType,success) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
			s.setString(1, getActorName());
			s.setTimestamp(2, getTimestamp());
			s.setString(3, getActivityType().toString());
			s.setBoolean(4, getSuccess());

			s.executeUpdate();

			try (ResultSet r = s.getGeneratedKeys()) {
				r.next();
				setId(r.getInt(1));
			}
		}
	}
	
	@Override
	protected String getTablename() {
		return TABLENAME;
	}
	@Override
	protected void validateEnumValues() {
		ActivityType.validate(getActivityType());		
	}
	
	public static String getCreateStatement() {
		return "CREATE TABLE "+TABLENAME+" (`id` serial primary key, `actorName` varchar(250), `timestamp` timestamp, `activityType` varchar(250), `success` boolean);";
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
}
