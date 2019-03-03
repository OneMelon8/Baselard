package JProjects.BaseInfoBot.commands.admin;

import java.util.Arrays;

import org.json.simple.JSONArray;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.files.SuitFileEditor;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Update extends Command {

	public Update(Bot bot) {
		super(bot, "update", "Developer command");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fire(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		User author = e.getAuthor();
		if (!Bot.admins.contains(author.getId())) {
			bot.sendMessage(e.getAuthor().getAsMention() + " permission denied :x:", e.getChannel());
			return;
		}
		if (args.length < 4) {
			bot.sendMessage(getHelpEmbeded(), e.getChannel());
			return;
		}

		try {
			JSONArray jarr = new JSONArray();
			jarr.addAll(Arrays.asList("amadan", "darr", "gorori", "grash", "kai", "magata", "xiao_chui", "alther",
					"clotho", "crescendo", "elektra", "hellmaster_blader", "hyper_infinity", "jinrai", "mecha_slayer",
					"starling_x_fighter", "weray", "zephyr"));
			SuitFileEditor.write("assault", jarr);

			jarr.clear();
			jarr.addAll(Arrays.asList("elder", "emma", "hera", "harmony", "ionne", "ajax", "amadeus", "breaking_dawn",
					"fantasia", "lachesis", "momentia", "pianissimo", "reta_tower", "stylet"));
			SuitFileEditor.write("support", jarr);

			jarr.clear();
			jarr.addAll(Arrays.asList("athlon", "muspel", "pygma", "sylph", "xiao_chen", "yata", "zenka", "antigone",
					"arpeggio", "baal", "dusk_wing", "gourai", "tai_shei", "veryd", "yeezus"));
			SuitFileEditor.write("bombardier", jarr);

			jarr.clear();
			jarr.addAll(Arrays.asList("glue", "ino", "merrows", "nimue", "pauler", "kusanagi", "atropos", "baselard",
					"crusader_raven", "electrux", "fortissimo", "garv", "oedipus", "megido_falcon"));
			SuitFileEditor.write("sniper", jarr);

			bot.sendMessage(author.getAsMention() + " successfully updated database! :white_check_mark:",
					e.getChannel());
		} catch (Exception ex) {
			bot.sendMessage(author.getAsMention() + " updating failed, check console for error.", e.getChannel());
			GeneralTools.logError(ex);
			ex.printStackTrace();
			return;
		}
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Update Template");
		builder.setDescription("Use the following template to update the local JSON database");
		builder.addField(
				new Field("Copy & Paste:", "```" + Messages.prefix + "update <file> <path> <object>" + "```", false));
		return builder.build();
	}
}
