package karsten;

import jukebox.Jukebox;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageHandler extends ListenerAdapter{
	private Jukebox jukebox;
	
	public MessageHandler() {
		jukebox = new Jukebox();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		// Only operate on text
		if (!event.isFromType(ChannelType.TEXT)) {
			return;
		}
		
		// Format a chat message
		ChatMessage chatMessage = new ChatMessage(event);
		
		// Handle received message
		switch(chatMessage.getKind()) {
		case IGNORED:
			break;
		case UNKNOWN:
			chatMessage.textChannel.sendMessage("Hva faen e det for en kommendo? Preov '**!help**'").queue();
			break;
		case PING:
			chatMessage.textChannel.sendMessage("Pong!").queue();
			break;
		case PLAY:
			chatMessage.textChannel.sendMessage("Är det en melodi du har där?").queue();
			jukebox.loadAndPlay(chatMessage.textChannel, chatMessage.argument, chatMessage.author);
			break;
		case YOUTUBE:
			chatMessage.textChannel.sendMessage("Låt mig se vad jag kan hitta ...").queue();
			jukebox.searchYoutube(event.getTextChannel(), chatMessage.argument, chatMessage.author);
			break;
		case SKIP:
			jukebox.skipTrack(chatMessage.textChannel);
			break;
		case STOP:
			jukebox.stop(chatMessage.textChannel);
			break;
		case UPVOTE:
			jukebox.giveVote(chatMessage.textChannel, chatMessage.author, 1);
			break;
		case DOWNVOTE:
			jukebox.giveVote(chatMessage.textChannel, chatMessage.author, -1);
			break;
		case REMOVE:
			//musicHandler.removeAt(chatMessage.argument);
			break;
		case LEAVE:
			jukebox.stop(chatMessage.textChannel);
			jukebox.leave(chatMessage.guild, chatMessage.author);
			break;
		case LIST:
			jukebox.list(chatMessage.textChannel);
			break;
		case HELP:
			printHelp(chatMessage.textChannel);
			break;
		default:
			System.out.println("Reached default case at message categorization");
			break;
		}
	}
	
	// TODO: Find a better way
	void printHelp(TextChannel channel) {
		String helpStr = "Tillgängliga kommandon: \n"
				+ "```md\n" // Just to highlight something - http or md
				+ "!ping        : pong \n"
				+ "!play <link> : Plays given YouTube <link> \n"
				+ "!yt <query>  : Search YouTube for <query> \n"
				+ "!skip        : Skip current song \n"
				+ "!stop        : Karsten stops \n"
				+ "!leave       : Karsten leaves voice channel \n"
				+ "```";
		channel.sendMessage(helpStr).queue();
	}
}
