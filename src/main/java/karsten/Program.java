package karsten;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import secrets.Secrets;

public class Program {
	public static void main(String[] args) {
		System.out.println("Karsten flyver ind fron pilsnar land!");
		
		try {
			JDA jda = new JDABuilder(AccountType.BOT)
					.setToken(Secrets.getDiscordToken())
					.addEventListener(new MessageHandler())
					.buildBlocking();
		}
		catch(LoginException e) {
			System.out.println("[Exception]: Login failed at main"); 
			e.printStackTrace();
		}
		catch(Exception e) { 
			System.out.println("[Exception]: Failed at creating JDA at main"); 
			e.printStackTrace();
		}
	}
}
