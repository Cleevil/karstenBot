package jukebox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import database.DbManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.managers.GuildManager;
import secrets.Secrets;

public class Jukebox {
	private final AudioPlayerManager playerManager;
	private final Map<Long, GuildMusicManager> musicManagers;
	
	public Jukebox() {
		this.musicManagers  = new HashMap<>();
		this.playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);
	}
	
	private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
		long guildId = Long.parseLong(guild.getId());
		GuildMusicManager musicManager = musicManagers.get(guildId);
		
		if (musicManager == null) {
			musicManager = new GuildMusicManager(playerManager, guild);
			musicManagers.put(guildId, musicManager);
		}
		
		guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
		
		return musicManager;
	}
	
	public void searchYoutube(final TextChannel channel, final String query, final User author) {
		String googleToken = Secrets.getGoogleToken();
		StringBuffer content = new StringBuffer();
		
		try {
			URL url = new URL("https://www.googleapis.com/youtube/v3/search?key="
								+googleToken+"&q="+URLEncoder.encode(query, "UTF-8")
								+"&part=snippet&maxResults=1&type=video");
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			
			
			int status = con.getResponseCode();
			if (status != 200) {
				return;
			}
			BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
			String inputLine;
			content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();
			con.disconnect();
		} catch (Exception e) 
			{ System.out.println("Search Youtube exception: " + e.getMessage()); }
		
		JSONObject json = new JSONObject(content.toString());
		
		// There might be no result
		if (json.getJSONArray("items").isNull(0)) {
			channel.sendMessage("Wat..").queue();
			return;
		}
		
		loadAndPlay(channel, 
				"https://www.youtube.com/watch?v="
				+ json.getJSONArray("items").getJSONObject(0).getJSONObject("id").get("videoId"), 
				author);
	}
	
	public void loadAndPlay(final TextChannel channel, final String trackUrl, User author) {
	    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
	    
	    System.out.println("YouTube ID: " + trackUrl);

	    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
	      @Override
	      public void trackLoaded(AudioTrack track) {
	        channel.sendMessage("Lägger till den här låten i kö: " + track.getInfo().title).queue();

	        play(channel.getGuild(), musicManager, track, author);
	      }

	      @Override
	      public void playlistLoaded(AudioPlaylist playlist) {
	        AudioTrack firstTrack = playlist.getSelectedTrack();

	        if (firstTrack == null) {
	          firstTrack = playlist.getTracks().get(0);
	        }

	        channel.sendMessage("Adding to queue " 
	        					+ firstTrack.getInfo().title 
	        					+ " (first track of playlist " 
	        					+ playlist.getName() + ")").queue();

	        play(channel.getGuild(), musicManager, firstTrack, author);
	      }

	      @Override
	      public void noMatches() {
	        channel.sendMessage("Ingenting hittades av: " + trackUrl).queue();
	      }

	      @Override
	      public void loadFailed(FriendlyException exception) {
	        channel.sendMessage("Det gick inte att spela: " + exception.getMessage()).queue();
	      }
	    });
	}
	
	private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, User author) {
		connectRelevantChannel(guild.getAudioManager(), author);
		
		Vote vote = new Vote(author.getId(), 1);
		
		// Insert vote for database
		DbManager.getInstance().updateBasedOnPlay(guild, author, track, vote);
		
	    musicManager.scheduler.queue(track, vote);
	}
	
	public void skipTrack(TextChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		AudioTrack track = musicManager.getPlayingTrack();
		musicManager.scheduler.nextTrack(track);
		
		// TODO: Make table for stat tracking in DB
		
		channel.sendMessage("Hoppa över till nästa låten.").queue();
	}
	
	public void stop(TextChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		musicManager.scheduler.stopAll();
		
		channel.sendMessage("Jag stoppar allt nu. Ursäkta mig..").queue();
	}
	
	public void list(TextChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		String stringQueue =  musicManager.scheduler.getQueueAsString();
		
		String currentList = "Current Tracks in queue:\n```";
		if (stringQueue.isEmpty()) {
			currentList += "Queue is empty";
			currentList += " ```";
		}
		else {
			currentList += stringQueue;
			currentList += "```";
		}
		channel.sendMessage(currentList).queue();
	}
	
	// For debug purposes
	private static void connectToFirstVoiceChannel(AudioManager audioManager) {
		if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
			for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
				audioManager.openAudioConnection(voiceChannel);
				break;
			}
		}
	}
	
	private static void connectRelevantChannel(AudioManager audioManager, User author) {
		if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
			for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
				for (Member mem : voiceChannel.getMembers()) {
					if (mem.getUser() == author) {
						audioManager.openAudioConnection(voiceChannel);
						break;
					}
				}
			}
		}
	}

	public void leave(Guild guild, User author) {
		for (VoiceChannel voice : guild.getVoiceChannels()) {
			for (Member mem : voice.getMembers()) {
				if (mem.getUser().getId().toString().equals("323258374160515072")) { // Karsten ID
					guild.getAudioManager().closeAudioConnection();
				}
			}
		}
	}

	public void giveVote(TextChannel textChannel, User author, int voteValue) {
		GuildMusicManager musicManager = getGuildAudioPlayer(textChannel.getGuild());
		
		// Check if currently playing
		if (!musicManager.scheduler.isPlaying())
		{
			textChannel.sendMessage("Nothing is playing - nothing to upvote").queue();
			return;
		}
		
		Vote vote = new Vote(author.getId(), voteValue);
		// Add vote to DB
		// TODO: is a specialised function needed for votes?
		DbManager.getInstance().updateBasedOnPlay(
				textChannel.getGuild(), 
				author, 
				musicManager.getPlayingTrack(), 
				vote);
		
		textChannel.sendMessage("Jag fick din omröstning. Aktuell poäng är: "
				+ "**" + musicManager.scheduler.getVoteTally(textChannel.getGuild()) + "**").queue();
	}
}
