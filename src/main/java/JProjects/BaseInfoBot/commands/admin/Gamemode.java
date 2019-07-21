package JProjects.BaseInfoBot.commands.admin;

import java.util.ArrayList;
import java.util.Random;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.BotConfig;
import JProjects.BaseInfoBot.tools.EnviroHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class Gamemode extends Command {

	public Gamemode(BaseInfoBot bot) {
		super(bot, "gamemode", new String[] { "gm" }, "Administrative action mode accessor");
	}

	ArrayList<String> admins = EnviroHandler.getAdministrators();
	String[] deathMessages = new String[] { " tried to swim in lava", " hugged a creeper", " blew up",
			" was struck by lightning", " was obliterated by Baselard's lasors. Ehehee rekt!", " was pricked to death",
			" drowned", " suffocated in a wall", " was squished too much", " experienced kinetic energy",
			" hit the ground too hard", " fell from a high place", " fell off a ladder", " fell off some vines",
			" was squashed by a falling anvil", " was killed by magic", " went up in flames", " burned to death",
			" was burnt to a crisp whilst fighting TkON", " walked into fire whilst fighting TkON",
			" went off with a bang", " discovered the floor was lava", " was fireballed by a ghast",
			" starved to death", " was poked to death by a sweet berry bush", " was killed trying to hurt Baselard",
			" was impaled by drowned", " withered away", " was pummeled by wither", " fell out of the world" };

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		String reaction = null;
		if (args.length == 0) {
			bot.reactQuestion(message);
			return;
		}
		if (args[0].equals("0") || args[0].equals("s") || args[0].equals("survival")) {
			reaction = Emotes.getId(Emotes.MINECRAFT_GRASS);
			if (admins.contains(author.getId()))
				toggleAdminMode(false, author.getId());
		} else if (args[0].equals("1") || args[0].equals("c") || args[0].equals("creative")) {
			reaction = Emotes.getId(Emotes.MINECRAFT_COMMAND_BLOCK);
			if (admins.contains(author.getId()))
				toggleAdminMode(true, author.getId());
		} else if (args[0].equals("2") || args[0].equals("a") || args[0].equals("adventure")) {
			reaction = Emotes.getId(Emotes.MINECRAFT_COBBLESTONE);
		} else if (args[0].equals("3") || args[0].equals("sp") || args[0].equals("spectator")) {
			reaction = Emotes.getId(Emotes.MINECRAFT_GLASS);
		} else {
			bot.sendMessage(author.getAsMention() + deathMessages[new Random().nextInt(deathMessages.length)], channel);
			return;
		}
		bot.addReaction(message, bot.getJDA().getEmoteById(reaction));
	}

	public void toggleAdminMode(boolean admin, String id) {
		if (admin)
			BaseInfoBot.admins.add(id);
		else
			BaseInfoBot.admins.remove(id);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Gamemode Template");
		builder.setDescription("Use the following template to change your game mode");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + " <mode>" + "```", false));
		return builder.build();
	}
}
