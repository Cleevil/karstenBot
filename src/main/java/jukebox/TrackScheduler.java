package jukebox;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.core.entities.Guild;

public class TrackScheduler extends AudioEventAdapter {
	private final AudioPlayer player;
	private final BlockingQueue<AudioTrack> queue;
	private DemocraticPlaylistDB democraticPlaylist;
	
	public TrackScheduler(AudioPlayer player, Guild guild) {
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
		democraticPlaylist = new DemocraticPlaylistDB(guild);
	}
	
	public void queue(AudioTrack track, Vote vote) {
		if (!player.startTrack(track, true)) {
			queue.offer(track);
		}
	}
	
	public void nextTrack(AudioTrack previousTrack) {
		// Play from queue if its not empty
		if (queue.peek() != null) {
			player.startTrack(queue.poll(), false);
			return;
		}
		// Otherwise go for the Democratic playlist
		if (democraticPlaylist.isEmpty()) {
			player.startTrack(queue.poll(), false);
			return;
		}
		AudioTrack track = democraticPlaylist.getARandomTrack(previousTrack);
		player.startTrack(track, false); // Will stop if null
	}
	
	public boolean isPlaying() {
		if (player.getPlayingTrack() != null) {
			return true;
		}
		return false;
	}
	
	public void stopAll() {
		queue.removeAll(queue);
		player.startTrack(queue.poll(), false);
	}
	
	// TODO: Is this even needed?
	public void removeAt(int position) {
		
	}
	
	/***
	 * Call to get a display friendly sting of the queue
	 * @return a printable string for the queue
	 */
	public String getQueueAsString() {
		String retString = "";
		if (queue.isEmpty()) {
			return retString;
		}
		
		int counter = 1;
		for (AudioTrack track : queue) {
			retString += counter++ + " - ";
			retString += track.getInfo().title;
			retString += "\n";
		}
		return retString;
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		// Remove from democratic playlist if it have too many downvotes
		//democraticPlaylist.evaluateTrack(track);
		democraticPlaylist.evaluateTrack(track);
			
		if (endReason.mayStartNext) {
			nextTrack(track);
		}
	}

	public int getVoteTally(Guild guild) {
		AudioTrack track = player.getPlayingTrack();
		int tally = democraticPlaylist.getVoteTally(track);
		return tally;
	}
	
	public final AudioTrack getPlayingTrack() {
		return player.getPlayingTrack();
	}
}
