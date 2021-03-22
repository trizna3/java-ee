package src.server.rdg;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import src.common.VravCommunicationUtil;
import src.server.jdbc.DBContext;

public class Actor extends RdgObject {

	public static final String TABLENAME = "TAB_trizna_actor";

	private String username;
	private String password;
	private UserLevel userLevel;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserLevel getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(UserLevel userLevel) {
		this.userLevel = userLevel;
	}

	@Override
	protected void update() throws SQLException {
		validateEnumValues();
		
		try (PreparedStatement s = DBContext.getConnection().prepareStatement("UPDATE " + getTablename() + " SET username = ?, password = ?, userLevel = ? WHERE id = ?")) {
			s.setString(1, getUsername());
			s.setString(2, getPassword());
			s.setString(3, getUserLevel().toString());
			s.setInt(4, getId());

			s.executeUpdate();
		}
	}

	@Override
	protected void insert() throws SQLException {
		validateEnumValues();
		
		try (PreparedStatement s = DBContext.getConnection().prepareStatement("INSERT INTO " + getTablename() + " (username,password,userLevel) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
			s.setString(1, getUsername());
			s.setString(2, getPassword());
			s.setString(3, getUserLevel().toString());

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
		UserLevel.validate(getUserLevel());
	}

	public static String getCreateStatement() {
		return "CREATE TABLE "+TABLENAME+" (`id` serial primary key, `username` varchar(250), `password` varchar(250), `userLevel` varchar(250));";
	}
	
	public static String getPrefillStatement() {
		return "INSERT INTO "+TABLENAME+" (username,password,userLevel) values ('"+VravCommunicationUtil.ADMIN_USERNAME+"','admin','ADMIN');";
	}
}
