package jukebox;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;

import database.DbManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ResumedEvent;

public class DemocraticPlaylistDB {
	private DbManager dbman = DbManager.getInstance();
	private Guild guild;
	private final AudioPlayerManager playerManager;
	
	AudioTrack retTrack = null;
	
	public DemocraticPlaylistDB(Guild guild) {
		this.guild = guild;
		this.playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);
	}
	
	public boolean isEmpty() {
		return dbman.isGuildTracksEmpty(guild);
	}
	

	public AudioTrack getARandomTrack(AudioTrack previousTrack) {
		CountDownLatch latch = new CountDownLatch(1);
		String trackID = "https://youtu.be/";
		trackID += dbman.getARandomTrack(guild, previousTrack); 
		
		AudioLoadHandler loadHandler = new AudioLoadHandler(latch);
		playerManager.loadItem(trackID, loadHandler);
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		AudioTrack retTrack = loadHandler.getTrack();
		
		return retTrack;
	}
	
	/***
	 * Midetidlig loesning
	 * @author Raphael
	 *
	 */
	class AudioLoadHandler implements AudioLoadResultHandler {
		CountDownLatch latch;
		AudioTrack retTrack;
		
		AudioLoadHandler(CountDownLatch latch) {
			super();
			this.latch = latch;
		}
		
		private void done() {
			latch.countDown();
		}
		
		public AudioTrack getTrack() {
			return retTrack;
		}

		@Override
		public void trackLoaded(AudioTrack track) {
			// TODO Auto-generated method stub
			retTrack = track;
			done();
		}

		@Override
		public void playlistLoaded(AudioPlaylist playlist) {
			// TODO Auto-generated method stub
			done();
		}

		@Override
		public void noMatches() {
			// TODO Auto-generated method stub
			System.out.println("DemocraticPlaylistDB found no match.");
			done();
		}

		@Override
		public void loadFailed(FriendlyException exception) {
			// TODO Auto-generated method stub
			System.out.println("DemocraticPlaylistDB failed in loading track.");
			done();
		}
		
	}
	
	/**
	 * Retrieves all votes for a track within a guild
	 * and returns their sum.
	 * @param track
	 * @return voteTally as an integer
	 */
	public int getVoteTally(AudioTrack track) {
		List<Integer> voteValues = dbman.getTrackVotes(track, guild);
		int voteSum = 0;
		for(int i : voteValues) {
			voteSum += i;
		}
		return voteSum;
	}
	
	/**
	 * Evaluates a given track based on received votes. 
	 * The track, and related votes, will be removed if 
	 * the sum of the votes are negative.
	 * @param track
	 */
	public void evaluateTrack(AudioTrack track) {
		int voteTally = getVoteTally(track);
		
		System.out.println("TRACK: " + track.getInfo().title +", VOTE SUM: " + voteTally);
		
		if (voteTally > -1) {
			return;
		}
		
		dbman.deleteTrackWithVotes(track, guild);
	}
}
