package jukebox;

public class Vote {
	String userId;
	int voteValue;
	
	public Vote(String userId, int vote) {
		this.userId = userId;
		this.voteValue = vote;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public int getVoteValue() {
		return voteValue;
	}
}
