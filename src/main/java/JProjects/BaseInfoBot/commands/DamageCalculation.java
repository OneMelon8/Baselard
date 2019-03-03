package JProjects.BaseInfoBot.commands;

import java.util.HashMap;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DamageCalculation extends Command {

	public DamageCalculation(Bot bot) {
		super(bot, "dmgcalc", new String[] { "dmg", "damagecalc", "dmgcalculator", "damagecalculator" },
				"Calculates the anticipated damage of attacks");
	}

	// b!dc <damage> <critical%> [s=suit/<p=pen & e=enemy>]

	// ATK: 1627 (+50%) 2338 after
	// Calculated DEF = 102.5
	// ATK: 268989 (+30%) expected = 349583.2 actual: 349507

	// Saved suits?

	@Override
	public void fire(MessageReceivedEvent e) {
		String[] msgArr = e.getMessage().getContentRaw().split(" ");
		if (msgArr.length < 3) {
			bot.sendMessage(getHelp(), e.getChannel());
			return;
		}
	}

	public HashMap<String, int[]> calculateDamage(double damage, double critical) {
		HashMap<String, int[]> output = new HashMap<String, int[]>();

		return output;
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Damage Calculation Template");
		builder.setDescription("Use the following template to calculate the damage");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + "dmgcalc" + "```", false));
		return builder.build();
	}
}
