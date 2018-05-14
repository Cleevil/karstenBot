package jukebox;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {
	public final AudioPlayer player;
	
	public final TrackScheduler scheduler;
	
	public GuildMusicManager(AudioPlayerManager manager) {
		player = manager.createPlayer();
		player.setVolume(10);
		scheduler = new TrackScheduler(player);
		player.addListener(scheduler);
	}
	
	public AudioPlayerSendHandler getSendHandler() {
		return new AudioPlayerSendHandler(player);
	}
}
