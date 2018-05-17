package database;

import java.sql.Connection;
import java.sql.DriverManager;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class DbManager {
	private static DbManager dbman = new DbManager();
	Connection c = null;
	
	private DbManager() {
	      try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         System.exit(0);
	      }
	      System.out.println("Opened database successfully");
	}
	
	public static DbManager getInstance() {
		return dbman;
	}
	
	private void registerTrack(AudioTrack track) {
		
	}
	
	private void deleteTrack(AudioTrack track) {
		
	}
	
	private void registerGuild() {
		
	}
	
	private void registerUser() {
		
	}
	
	private void registerVote() {
		
	}
}
