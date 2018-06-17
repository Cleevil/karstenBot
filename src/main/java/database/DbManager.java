package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import jukebox.Vote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public class DbManager {
	private static DbManager dbman = new DbManager();
	Connection c = null;
	String dbFileName = "test.db";
	DebugPrinter debugPrinter = new DebugPrinter(false);
	
	private DbManager() {
	      try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         System.exit(0);
	      }
	      debugPrinter.simplePrint("Opened database successfully.");
	}
	
	public static DbManager getInstance() {
		return dbman;
	}
	
	private void tryInsert(Guild guild) {
		Statement stmt = null;
		DebugPrintMsg debugMsg = new DebugPrintMsg();
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
			c.setAutoCommit(false);
			
			debugMsg.setPurpose("Conected for guild insertion");
			
			stmt = c.createStatement();
			String sql = "INSERT INTO guilds (guildID,name) "
					+ "VALUES ("+guild.getId()+",'"+guild.getName()+"');";
			
			debugMsg.setStatement(sql);
			
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			debugMsg.setStatus("SUCCESS!");
			
		} catch(Exception e) {
			if( e.getMessage().contains("UNIQUE constraint failed")) {
				debugMsg.setStatus("SUCCESS");
			} else {
				debugMsg.setStatus("FAILED");
				debugMsg.setErrorMsg(e.getMessage());
			}
		}
		try {
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		debugPrinter.print(debugMsg);
	}
	
	private void tryInsert(AudioTrack track) {
		Statement stmt = null;
		DebugPrintMsg debugMsg = new DebugPrintMsg();
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
			c.setAutoCommit(false);
			
			debugMsg.setPurpose("Conected for track insertion");
			
			stmt = c.createStatement();
			String sql = "INSERT INTO tracks (trackID,title) "
					+ "VALUES ( '"+track.getIdentifier()+"' ,"
							+ "'"+track.getInfo().title.replace("'", "")+"');";
			debugMsg.setStatement(sql);
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			
			debugMsg.setStatus("SUCCESS!");
			
		} catch(Exception e) {
			if( e.getMessage().contains("UNIQUE constraint failed")) {
				debugMsg.setStatus("SUCCESS");
			} else {
				debugMsg.setStatus("FAILED");
				debugMsg.setErrorMsg(e.getMessage());
			}
		}
		try {
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		debugPrinter.print(debugMsg);
	}

	private void tryInsert(User author) {
		Statement stmt = null;
		DebugPrintMsg debugMsg = new DebugPrintMsg();
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
			c.setAutoCommit(false);
			
			debugMsg.setPurpose("Conected for user insertion");
			
			stmt = c.createStatement();
			String sql = "INSERT INTO users (userID,name) "
					+ "VALUES ( "+author.getId()+" ,'"+author.getName()+"');";
			
			debugMsg.setStatement(sql);
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			debugMsg.setStatus("SUCCESS!");
			
		} catch(Exception e) {
			if( e.getMessage().contains("UNIQUE constraint failed")) {
				debugMsg.setStatus("SUCCESS");
			} else {
				debugMsg.setStatus("FAILED");
				debugMsg.setErrorMsg(e.getMessage());
			}
		}
		try {
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		debugPrinter.print(debugMsg);
	}

	/**
	 * More information is needed for this insertion since
	 * the vote table contain multiple foreign keys.
	 * @param guild		for foreign key
	 * @param author	for foreign key
	 * @param track		for foreign key
	 * @param vote		for insertion of vote
	 */
	private void tryInsertVote(Guild guild, User author, AudioTrack track, Vote vote) {
		Statement stmt = null;
		DebugPrintMsg debugMsg = new DebugPrintMsg();
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
			c.setAutoCommit(false);
			
			debugMsg.setPurpose("Conected for vote insertion");
			
			stmt = c.createStatement();
			String sql = "INSERT OR REPLACE INTO votes (value, guildID, trackID, userID) "
					+ "VALUES ( "+ 		 vote.getVoteValue() +	 ","
								 + 		 guild.getId() + 		 ","
								 + "'" + track.getIdentifier() + "',"
								 + 		 author.getId() + 		 ");";

			debugMsg.setStatement(sql);
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			debugMsg.setStatus("SUCCESS!");
			
		} catch(Exception e) {
			debugMsg.setStatus("FAILED!");
			System.out.println(e.getMessage());
		}
		try {
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		debugPrinter.print(debugMsg);
	}

	public void updateBasedOnPlay(Guild guild, User author, AudioTrack track, Vote vote) {
		tryInsert(guild);
		tryInsert(author);
		tryInsert(track);
		tryInsertVote(guild, author, track, vote);
	}

	public boolean isGuildTracksEmpty(Guild guild) {
		Statement stmt = null;
		boolean retBool = true;
		DebugPrintMsg debugMsg = new DebugPrintMsg();
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
			c.setAutoCommit(false);
			
			debugMsg.setPurpose("Conected for IS GUILD TRACKS EMPTY");
			
			stmt = c.createStatement();
			String sql = "SELECT COUNT(value) FROM votes "
					+ "WHERE guildID =" + guild.getId() + ";";

			debugMsg.setStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				retBool = false;
			}
			
			stmt.close();
			c.commit();
			debugMsg.setStatus("SUCCESS!");
		} catch(Exception e) {
			debugMsg.setStatus("FAILED!");
			System.out.println(e.getMessage());
			
		}
		try {
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		debugPrinter.print(debugMsg);
		return retBool;
	}

	/**
	 * Fetch a track for the given guild.
	 * @param guild
	 * @param previousTrack - Do not fetch the most recent track
	 * @return string - the track identifier
	 */
	public String getARandomTrack(Guild guild, AudioTrack previousTrack) {
		Statement stmt = null;
		String retString = "empty";
		DebugPrintMsg debugMsg = new DebugPrintMsg();
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
			c.setAutoCommit(false);
			
			debugMsg.setPurpose("Conected for retreiving a random track");
			
			stmt = c.createStatement();
			String sql = "SELECT trackID FROM votes "
					+ "WHERE votes.guildID =" + guild.getId() + " "
					+ "AND votes.trackID != '" + previousTrack.getIdentifier() + "' "
					+ "ORDER BY RANDOM() LIMIT 1;";

			debugMsg.setStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				retString = rs.getString("trackID");
			}
			
			stmt.close();
			debugMsg.setStatus("SUCCESS!");
		} catch(Exception e) {
			debugMsg.setStatus("FAILED!");
			System.out.println(e.getMessage());
			
		}
		try {
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		debugPrinter.print(debugMsg);
		return retString;
	}

	public List<Integer> getTrackVotes(AudioTrack track, Guild guild) {
		Statement stmt = null;
		List<Integer> retValues = new ArrayList<Integer>();
		DebugPrintMsg debugMsg = new DebugPrintMsg();
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
			c.setAutoCommit(false);
			
			debugMsg.setPurpose("Conected for vote retrieval");
			
			stmt = c.createStatement();
			String sql = "SELECT value FROM votes "
					+ "WHERE guildID ="+guild.getId()+";";

			debugMsg.setStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				retValues.add(rs.getInt("value"));
			}
			
			stmt.close();
			debugMsg.setStatus("SUCCESS!");
		} catch(Exception e) {
			debugMsg.setStatus("FAILED!");
			System.out.println(e.getMessage());
		}
		try {
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		debugPrinter.print(debugMsg);
		
		return retValues;
	}

	public void deleteTrackWithVotes(AudioTrack track, Guild guild) {
		Statement stmt = null;
		DebugPrintMsg debugMsg = new DebugPrintMsg();
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
			c.setAutoCommit(false);
			
			debugMsg.setPurpose("Conected for removal of votes");
			
			stmt = c.createStatement();
			String sql = "DELETE FROM votes "
					+ "WHERE guildID ="+guild.getId()+" "
					+ "AND trackID ='"+track.getIdentifier()+"';";

			debugMsg.setStatement(sql);
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			debugMsg.setStatus("SUCCESS!");
		} catch(Exception e) {
			debugMsg.setStatus("FAILED!");
			System.out.println(e.getMessage());
		}
		try {
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		debugPrinter.print(debugMsg);
	}
	
	private class DebugPrintMsg {
		private String purpose;
		private String statement;
		private String status;
		private String errorMsg;
		
		public void setPurpose(String msg) {
			purpose = msg;
		}
		public void setStatement(String msg) {
			statement = msg;
		}
		public void setStatus(String msg) {
			status = msg;
		}
		public void setErrorMsg(String msg) {
			errorMsg = msg;
		}
		public void print() {
			String msg = "\n" + purpose + "\n" + statement + "\n";
			if (errorMsg != null) {
				msg += errorMsg + "\n";
			}
			msg += status;
			System.out.println(msg);
		}
	}
	
	private class DebugPrinter {
		boolean debugStatus = false;

		DebugPrinter(boolean status) {
			debugStatus = status;
		}
		
		public void simplePrint(String msg) {
			if (!debugStatus) { return;	}
			System.out.println(msg);
		}
		
		public void print(DebugPrintMsg msg) {
			if (!debugStatus) { return;	}
			msg.print();
		}
	}
}
