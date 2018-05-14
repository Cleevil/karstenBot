package jukebox;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter {
	private final AudioPlayer player;
	private final BlockingQueue<AudioTrack> queue;
	private DemocraticPlaylist democraticPlaylist;
	
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
		democraticPlaylist = new DemocraticPlaylist();
	}
	
	public void queue(AudioTrack track, Vote vote) {
		democraticPlaylist.addTrack(track, vote);
		
		if (!player.startTrack(track, true)) {
			queue.offer(track);
		}
	}
	
	public void nextTrack() {
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
		AudioTrack track = democraticPlaylist.getATrack();
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
		democraticPlaylist.evaluateTrack(track);
			
		if (endReason.mayStartNext) {
			nextTrack();
		}
	}

	public void setVote(Vote vote) {
		AudioTrack curTrack = player.getPlayingTrack();
		democraticPlaylist.castVote(curTrack, vote);
	}

	public int getVotes() {
		AudioTrack track = player.getPlayingTrack();
		return democraticPlaylist.getVotes(track);
	}
}
