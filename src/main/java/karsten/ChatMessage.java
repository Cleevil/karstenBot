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
		System.out.println("Message: " + message);
		
		// Ignore message if it is not meant for Karsten
		if (!message.startsWith("!") || author.isBot()) {
			msgKind = MessageKind.IGNORED;
		}
		else if (message.equals("!ping")) {
			msgKind = MessageKind.PING;
		}
		// Must also have a search query
		else if (message.startsWith("!yt")) { 
			if (message.length() < 5) {
				channel.sendMessage("Det eer et svii!ne dorligt link det der").queue();
				msgKind = MessageKind.IGNORED;
			}
			argument = message.substring(4, message.length());
			msgKind = MessageKind.YOUTUBE;
		}
		else if (message.startsWith("!play")) {
			
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
		else if (message.startsWith("!rm") && message.length() < 5) {
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
		
		System.out.printf("[TRIPLE]: author: %s, msg: %s, channel: %s \n", author.getName(), message, channel.getName());
	}
	MessageKind getKind() {
		return msgKind;
	}
}
