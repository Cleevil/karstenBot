package karsten;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import jukebox.Jukebox;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;

public class MessageHandler extends ListenerAdapter{
	private Jukebox musicHandler;
	
	public MessageHandler() {
		musicHandler = new Jukebox();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		JDA jda = event.getJDA();
		long responseNumber = event.getResponseNumber();
		
		User author = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		Guild guild = event.getGuild();
		
		String msg = message.getContentDisplay();
		boolean bot = author.isBot();
		
		// Ignore message if it is not meant for Karsten
		if (msg.charAt(0) != '!') {
			return;
		}
		
		System.out.printf("[TRIPLE]: author: %s, msg: %s, channel: %s \n", author.getName(), message, channel.getName());
		
		// Make modules for types of input
		if (event.isFromType(ChannelType.TEXT)) {
			TextChannel textChannel = event.getTextChannel();
			Member member = event.getMember(); // Guild specific info about user
			
			// Could check for nickname here
		}
		else if (event.isFromType(ChannelType.PRIVATE)) {
			PrivateChannel privateChannel = event.getPrivateChannel();
			System.out.printf("[PRIVATE]<%s>: %s\n", author.getName(), msg);
		}
		
		// Handle received message
		if (msg.equals("!ping")) {
			System.out.println("YES I GOT HERE");
			channel.sendMessage("Pong!").queue();
		}
		else if (msg.startsWith("!yt")) {
			if (msg.length() < 5) {
				channel.sendMessage("Det eer et svii!ne dorligt link det der").queue();
				return;
			}
			musicHandler.searchYoutube(event.getTextChannel(), msg.substring(4, msg.length()), author);
		}
		else if (msg.startsWith("!play")) {
			musicHandler.loadAndPlay(event.getTextChannel(), "https://youtu.be/mveqm_Snbzs", author);
		}
		else if (msg.startsWith("!skip")) {
			musicHandler.skipTrack(event.getTextChannel());
		}
		else if (msg.startsWith("!list")) {
			musicHandler.list(event.getTextChannel());
		}
		else if (msg.startsWith("!stop")) {
			musicHandler.stop(event.getTextChannel());
		}
		else if (msg.startsWith("!leave")) {
			musicHandler.stop(event.getTextChannel());
			musicHandler.leave(guild, author);
		}
		else {
			channel.sendMessage("Hva faen e det for en kommendo?").queue();
		}
	}
}
