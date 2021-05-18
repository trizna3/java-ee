package src.server.rdg;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import src.server.jdbc.DBContext;

public abstract class RdgObject {

	abstract protected void update() throws SQLException;
	abstract protected void insert() throws SQLException;	
	abstract protected String getTablename();
	abstract protected void validateEnumValues();
	
	
	private Integer id;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void store() {
		try {
			if (id == null) {
				insert();
			} else {
				update();
			}
		} catch (SQLException e) {
			System.out.println("Error storing rdg object");
			e.printStackTrace();
		}
	}
	
	protected void delete() throws SQLException {
		try (PreparedStatement s = DBContext.getConnection().prepareStatement("DELETE from " + getTablename() + " WHERE id = ?")) {
			s.setInt(1, getId());
			s.executeUpdate();
		}
	}
}
