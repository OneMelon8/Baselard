package JProjects.BaseInfoBot.commands.admin;

import java.util.ArrayList;
import java.util.Random;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.tools.EnviroHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Gamemode extends Command {

	public Gamemode(Bot bot) {
		super(bot, "gamemode", "Administrative action mode accessor");
	}

	// Me Tkon Tater
	ArrayList<String> admins = EnviroHandler.getAdministrators();
	String[] deathMessages = new String[] { " tried to swim in lava", " attempted to hug a creeper",
			" was obliterated by Baselard's lasors. Ehehee rekt!", " fell out of the world" };

	@Override
	public void fire(MessageReceivedEvent e) {
		User user = e.getAuthor();
		MessageChannel ch = e.getChannel();
		String[] args = e.getMessage().getContentRaw().split(" ");
		String mode = "";
		boolean changed = false;
		if (args[1].equals("0") || args[1].equals("s") || args[1].equals("survival")) {
			mode = "survival";
			if (admins.contains(user.getId())) {
				// Administration mode off
				toggleAdminMode(false, user.getId());
				changed = true;
			}
		} else if (args[1].equals("1") || args[1].equals("c") || args[1].equals("creative")) {
			mode = "creative";
			if (admins.contains(user.getId())) {
				// Administration mode on?
				toggleAdminMode(true, user.getId());
				changed = true;
			}
		} else if (args[1].equals("2") || args[1].equals("a") || args[1].equals("adventure")) {
			mode = "adventure";
		} else if (args[1].equals("3") || args[1].equals("sp") || args[1].equals("spectator")) {
			mode = "spectator";
		} else {
			bot.sendMessage(user.getAsMention() + deathMessages[new Random().nextInt(deathMessages.length)], ch);
			return;
		}
		bot.sendMessage(
				user.getAsMention() + " your game mode has been set to ***" + mode + " mode***" + (changed ? "." : ""),
				ch);
	}

	public void toggleAdminMode(boolean admin, String id) {
		if (admin)
			Bot.admins.add(id);
		else
			Bot.admins.remove(id);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Gamemode Template");
		builder.setDescription("Use the following template to change game mode");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + "gamemode <mode>" + "```", false));
		return builder.build();
	}
}
