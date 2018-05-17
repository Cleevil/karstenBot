package karsten;

import javax.security.auth.login.LoginException;

import database.DbManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import secrets.Secrets;

public class Program {
	public static void main(String[] args) {
		try {
			JDA jda = new JDABuilder(AccountType.BOT)
					.setToken(Secrets.getDiscordToken())
					.addEventListener(new MessageHandler())
					.buildBlocking();
			System.out.println("Karsten flyver ind fron pilsnar land!");
		}
		catch(LoginException e) {
			System.out.println("[Exception]: Login failed at main"); 
			e.printStackTrace();
		}
		catch(Exception e) { 
			System.out.println("[Exception]: Failed at creating JDA at main"); 
			e.printStackTrace();
		}
		
		DbManager dbman = DbManager.getInstance();
	}
}
