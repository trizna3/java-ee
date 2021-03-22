package src.server.rdg;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import src.server.jdbc.DBContext;

public class ActorDAO {

	public static Actor getActorByName(String actorName) throws Exception {
		try (PreparedStatement s = DBContext.getConnection().prepareStatement("SELECT * FROM "+Actor.TABLENAME+" WHERE username = ?")) {
            s.setString(1, actorName);

            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    Actor actor = new Actor();

                    actor.setId(r.getInt("id"));
                    actor.setUsername(r.getString("username"));
                    actor.setPassword(r.getString("password"));
                    actor.setUserLevel(UserLevel.valueOf(r.getString("userLevel")));

                    return actor;
                }
            }
        }
		return null;
	}
}
