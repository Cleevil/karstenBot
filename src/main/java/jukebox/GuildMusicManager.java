package jukebox;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.entities.Guild;

public class GuildMusicManager {
	public final AudioPlayer player;
	
	public final TrackScheduler scheduler;
	
	public GuildMusicManager(AudioPlayerManager manager, Guild guild) {
		player = manager.createPlayer();
		player.setVolume(10);
		scheduler = new TrackScheduler(player, guild);
		player.addListener(scheduler);
	}

	public AudioPlayerSendHandler getSendHandler() {
		return new AudioPlayerSendHandler(player);
	}
	
	public final AudioTrack getPlayingTrack() {
		return scheduler.getPlayingTrack();
	}
}
