package JProjects.BaseInfoBot.commands.admin;

import java.util.ArrayList;
import java.util.Random;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.tools.EnviroHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
	public void onCommand(MessageReceivedEvent e) {
		User user = e.getAuthor();
		MessageChannel ch = e.getChannel();
		Message msg = e.getMessage();
		String[] args = msg.getContentRaw().split(" ");
		String reaction = null;
		if (args.length <= 1) {
			bot.reactQuestion(msg);
			return;
		}
		if (args[1].equals("0") || args[1].equals("s") || args[1].equals("survival")) {
			reaction = Emotes.getId(Emotes.MINECRAFT_GRASS);
			if (admins.contains(user.getId()))
				toggleAdminMode(false, user.getId());
		} else if (args[1].equals("1") || args[1].equals("c") || args[1].equals("creative")) {
			reaction = Emotes.getId(Emotes.MINECRAFT_COMMAND_BLOCK);
			if (admins.contains(user.getId()))
				toggleAdminMode(true, user.getId());
		} else if (args[1].equals("2") || args[1].equals("a") || args[1].equals("adventure")) {
			reaction = Emotes.getId(Emotes.MINECRAFT_COBBLESTONE);
		} else if (args[1].equals("3") || args[1].equals("sp") || args[1].equals("spectator")) {
			reaction = Emotes.getId(Emotes.MINECRAFT_GLASS);
		} else {
			bot.sendMessage(user.getAsMention() + deathMessages[new Random().nextInt(deathMessages.length)], ch);
			return;
		}
		bot.addReaction(msg, bot.getJDA().getEmoteById(reaction));
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
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Gamemode Template");
		builder.setDescription("Use the following template to change game mode");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.PREFIX + command + " <mode>" + "```", false));
		return builder.build();
	}
}
