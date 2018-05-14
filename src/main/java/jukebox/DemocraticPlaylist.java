package jukebox;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class DemocraticPlaylist {
	private Map<String, AudioTrackWithVotes> playlist = new LinkedHashMap<String, AudioTrackWithVotes>();
	
	public boolean isEmpty() {
		return playlist.isEmpty();
	}
	
	public AudioTrack getATrack() {
		if (playlist.isEmpty()) {
			return null;
		}
		
		Object[] tracks = playlist.keySet().toArray();
		
		String trackIdentifier = (String)tracks[new Random().nextInt(tracks.length)];
		
		// Debug
		System.out.println("\n[Debug] Democratic playlist:");
		for (String trk : playlist.keySet()) {
			System.out.println(trk + ", Votes: " + playlist.get(trk).getVotes());
		}
		
		return playlist.get(trackIdentifier).track.makeClone();
	}
	
	public void addTrack(AudioTrack track, Vote vote) {
		// Does the track already exist?
		if (playlist.containsKey(track.getIdentifier())) {
			castVote(track, vote);
		}
		else {
			AudioTrackWithVotes trackAndVotes = new AudioTrackWithVotes(track, vote);
			playlist.put(track.getIdentifier(), trackAndVotes);
		}
	}
	
	public void castVote(AudioTrack track, Vote vote) {
		// Add track if its not in the playlist
		if (playlist.get(track.getIdentifier()) == null) {
			AudioTrackWithVotes trackAndVotes = new AudioTrackWithVotes(track, vote);
			playlist.put(track.getIdentifier(), trackAndVotes);
		}
		else {
			playlist.get(track.getIdentifier()).addVote(vote);
		}
	}
	
	public void evaluateTrack(AudioTrack track) {
		int totalVotes = playlist.get(track.getIdentifier()).getVotes();
		if (totalVotes < 0) {
			playlist.remove(track.getIdentifier());
		}
	}
	public int getVotes(AudioTrack track) {
		return playlist.get(track.getIdentifier()).getVotes();
	}
	
	private class AudioTrackWithVotes {
		private AudioTrack track;
		private List<Vote> votes;
		
		public AudioTrackWithVotes(AudioTrack track, Vote vote) {
			this.track = track;
			votes = new LinkedList<Vote>();
			this.votes.add(vote);
		}
		
		public int getVotes() {
			int result = 0;
			for (Vote vote : votes) {
				result += vote.voteValue;
			}
			return result;
		}

		public void addVote(Vote vote) {
			boolean isReplaced = votes.stream().anyMatch(p -> {
				if(p.userId.equals(vote.userId)) {
					p.voteValue = vote.voteValue;
					return true;
				}
				else{
					return false;
				}
			});
			if (!isReplaced) {
				votes.add(vote);
			}
		}
	}
}
