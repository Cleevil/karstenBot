package karsten;

import jukebox.Jukebox;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageHandler extends ListenerAdapter{
	private Jukebox musicHandler;
	
	public MessageHandler() {
		musicHandler = new Jukebox();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		// Karsten only understand text
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
			chatMessage.textChannel.sendMessage("Hva faen e det for en kommendo?").queue();
			break;
		case PING:
			chatMessage.textChannel.sendMessage("Pong!").queue();
			break;
		case PLAY:
			musicHandler.loadAndPlay(chatMessage.textChannel, "https://youtu.be/mveqm_Snbzs", chatMessage.author);
			break;
		case YOUTUBE:
			musicHandler.searchYoutube(event.getTextChannel(), chatMessage.argument, chatMessage.author);
			break;
		case SKIP:
			musicHandler.skipTrack(chatMessage.textChannel);
			break;
		case STOP:
			musicHandler.stop(chatMessage.textChannel);
			break;
		case REMOVE:
			//TO DIS
			break;
		case LEAVE:
			musicHandler.stop(chatMessage.textChannel);
			musicHandler.leave(chatMessage.guild, chatMessage.author);
			break;
		case LIST:
			musicHandler.list(chatMessage.textChannel);
			break;
		case HELP: // TODO: Should reply with a description of available commands
			chatMessage.textChannel.sendMessage("To be implemented.").queue();
			break;
		default:
			System.out.println("Reached default case at message categorization");
			break;
		}
	}
}
