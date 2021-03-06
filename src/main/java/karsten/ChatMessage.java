package karsten;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

enum MessageKind { 
	IGNORED,
	UNKNOWN,
	PING,
	PLAY,
	YOUTUBE,
	SKIP,
	STOP,
	UPVOTE,
	DOWNVOTE,
	REMOVE,
	LEAVE,
	LIST,
	HELP
}

public class ChatMessage {
	private MessageKind msgKind;
	String argument = "";
	
	User author;
	MessageChannel channel;
	TextChannel textChannel;
	Guild guild; 
	
	public ChatMessage(MessageReceivedEvent event) {
		author  	= event.getAuthor();
		channel		= event.getChannel();
		textChannel = event.getTextChannel();
		guild   	= event.getGuild();
		
		String message = event.getMessage().getContentDisplay();
		
		// Ignore message if it is not meant for Karsten
		if (!message.startsWith("!") || author.isBot()) {
			msgKind = MessageKind.IGNORED;
		}
		else if (message.equals("!ping")) {
			msgKind = MessageKind.PING;
		}
		// Must also have a search query
		else if (message.startsWith("!yt") || message.startsWith("!youtube")) { 
			if (message.length() < 5) {
				channel.sendMessage("Det eer et svii!ne dorligt link det der").queue();
				msgKind = MessageKind.IGNORED;
				return;
			}
			argument = message.substring(4, message.length());
			msgKind = MessageKind.YOUTUBE;
		}
		else if (message.startsWith("!play")) {
			if (message.length() > 6) { // TODO: Check if yt link to song or playlist
				argument = message.substring(6, message.length());
			}
			else { // Default to medium song
				argument = "https://youtu.be/mveqm_Snbzs";
			}
			msgKind = MessageKind.PLAY;
		}
		else if (message.startsWith("!skip")) {
			msgKind = MessageKind.SKIP;
		}
		else if (message.startsWith("!list")) {
			msgKind = MessageKind.LIST;
		}
		else if (message.startsWith("!stop")) {
			msgKind = MessageKind.STOP;
		}
		else if (message.startsWith("!uvote") || message.startsWith("!upvote") || message.startsWith("!uv")) {
			msgKind = MessageKind.UPVOTE;
		}
		else if (message.startsWith("!dvote") || message.startsWith("!downvote") || message.startsWith("!dv")) {
			msgKind = MessageKind.DOWNVOTE;
		}
		else if (message.startsWith("!remove")) { // TODO: finish this if its really needed
			if (message.length() < 9) {
				channel.sendMessage("Hva faen ska jeg dog fjerne?").queue();
				msgKind = MessageKind.IGNORED;
				return;
			}
			argument = message.substring(8, message.length());
			int foo = 0;
			try {
			     foo = Integer.parseInt(argument);
			     System.out.println("YES! The number is: " + foo);
			} catch (NumberFormatException e) {
			    System.out.println("Wrong number");
			}
			msgKind = MessageKind.REMOVE;
		}
		else if (message.startsWith("!leave")) {
			msgKind = MessageKind.LEAVE;
		}
		else if (message.startsWith("!help")) {
			msgKind = MessageKind.HELP;
		}
		else {
			msgKind = MessageKind.UNKNOWN;
		}
		
		if (msgKind != MessageKind.IGNORED) {
			System.out.printf("[TRIPLE]: Author: %s,\tmsg: %s,\tChannel: %s \n", author.getName(), message, channel.getName());
		}
	}
	MessageKind getKind() {
		return msgKind;
	}
}
