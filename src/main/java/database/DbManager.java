package database;

import java.sql.*;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import jukebox.Vote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

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
	
	private void tryInsert(Guild guild) {
		Statement stmt = null;
		
		try {
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void tryInsert(Vote vote) {
		// TODO Auto-generated method stub
		
	}

	private void tryInsert(AudioTrack track) {
		// TODO Auto-generated method stub
		
	}

	private void tryInsert(User author) {
		// TODO Auto-generated method stub
		
	}
	
	public void updateBasedOnPlay(Guild guild, User author, AudioTrack track, Vote vote) {
		tryInsert(guild);
		tryInsert(author);
		tryInsert(track);
		tryInsert(vote);
	}
}
