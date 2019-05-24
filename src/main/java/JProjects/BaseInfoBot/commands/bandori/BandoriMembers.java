package JProjects.BaseInfoBot.commands.bandori;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BandoriMembers extends Command {

	public BandoriMembers(Bot bot) {
		super(bot, "member", new String[] { "members" }, "Shows the members");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		MessageChannel ch = e.getChannel();
		bot.sendMessage(
				"<:kasumi_toyama:580848806057476106> <:arisa_ichigaya:580848805998493706> <:tae_hanazono:580848806141362186> <:saaya_yamabuki:580848806166396928> <:rimi_ushigome:580848806086705173>\r\n"
						+ "<:ran_mitake:580849036165382154> <:moca_aoba:580849036031164416> <:tomoe_udagawa:580849036387549195> <:tsugumi_hazawa:580849036387549296> <:himari_uehara:580849036102467604>\r\n"
						+ "<:kokoro_tsurumaki:580849364444905473> <:hagumi_kitazawa:580849364105166918> <:misaki_okusawa:580849364570734592> <:kanon_matsubara:580849364755415094> <:kaoru_seta:580849364407156948>\r\n"
						+ "<:aya_maruyama:580849820789506068> <:chisato_shirasagi:580849821011935243> <:maya_yamato:580849820647030785> <:eve_wakamiya:580849820739043358> <:hina_hikawa:580849820412149814>\r\n"
						+ "<:yukina_minato:580850110267785226> <:lisa_imai:580850110557061120> <:ako_udagawa:580850110506729493> <:sayo_hikawa:580850110364385297> <:rinko_shirokane:580850110590746626>\r\n",
				ch);
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Bandori Events Query Template");
		builder.setDescription("Use the following template to run the Bandori event query");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + command + " [name]```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.deleteCharAt(sb.length() - 1);
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + Messages.prefix + command + " bushido```", false));
		return builder.build();
	}
}
