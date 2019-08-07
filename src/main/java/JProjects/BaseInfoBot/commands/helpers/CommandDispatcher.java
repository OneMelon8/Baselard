package JProjects.BaseInfoBot.commands.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.Help;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.tools.GeneralTools;
import JProjects.BaseInfoBot.tools.StringTools;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandDispatcher {

	public static HashMap<String, String[]> registeredCommands = new HashMap<String, String[]>();
	public static HashMap<String, Command> registeredListeners = new HashMap<String, Command>();

	private static HashMap<String, Long> cooldown = new HashMap<String, Long>();

	private static final String[] botResponses = new String[] {
			"Hmm, you know what? Lets talk like this so the hoomans doesn't understand us >w<", "Beep boop beep boop!",
			"Have you tried binary yet?", "Kono tensei Base-sama!" };
	private static final Random r = new Random();

	private static final String[] bdResponses = new String[] {
			"Thanks! Don't forget to tell my master happy birthday too!",
			Emotes.KOKORON_SPARKLE + " Here's a slice of cake~",
			"Join me in the adventure of making the world smile!" };

	private static boolean birthdayCheck(MessageReceivedEvent e) {
		String msg = e.getMessage().getContentRaw().toLowerCase();
		List<Member> mentioned = e.getMessage().getMentionedMembers();
		boolean contains = false;
		for (Member m : mentioned) {
			if (!m.getUser().getId().equals(BotConfig.BOT_ID))
				continue;
			contains = true;
			break;
		}
		if (!contains)
			return false;
		if (!msg.contains("happy") && !msg.contains("hpy"))
			return false;
		if (!msg.contains("birthday") && !msg.contains("bd") && !msg.contains("bday"))
			return false;

		App.bot.sendMessage(e.getAuthor().getAsMention() + " " + bdResponses[r.nextInt(bdResponses.length)],
				e.getChannel());
		return true;
	}

	/**
	 * Tries to process the command (if registered)
	 * 
	 * @param e - the {@link MessageReceivedEvent} instance
	 */
	public static void fire(MessageReceivedEvent e) {
		String msg = e.getMessage().getContentRaw();
		if (birthdayCheck(e))
			return;
		if (!msg.startsWith(BotConfig.PREFIX))
			return;

		if (e.getAuthor().isBot()) {
			App.bot.sendMessage(e.getAuthor().getAsMention() + " "
					+ GeneralTools.toBinary(botResponses[r.nextInt(botResponses.length)]), e.getChannel());
			return;
		}
		String[] msgArr = msg.split(" ");
		String userCmd = msgArr[0].substring(BotConfig.PREFIX.length()).toLowerCase();
		System.out.println(GeneralTools.getTime() + " >> " + e.getAuthor().getAsTag() + " executed " + msg);

		if (ChatEventHandler.mute && userCmd.equalsIgnoreCase("toggle")) {
			registeredListeners.get("toggle").onCommand(e.getAuthor(), userCmd, null, e.getMessage(), e.getChannel(),
					e.getGuild());
			return;
		}
		if (ChatEventHandler.mute)
			return;

		if (e.getChannel().getId().equals("562766797032652800")
				&& (!userCmd.equals("card") && !userCmd.equals("cards")))
			return;

		String authorId = e.getAuthor().getId();
		boolean isAdmin = BaseInfoBot.admins.contains(authorId);
		int cooldownTime = 3000; // Milliseconds
		if (cooldown.containsKey(authorId)) {
			long msLeft = cooldown.get(authorId) + cooldownTime - System.currentTimeMillis();
			if (msLeft > 0 && !isAdmin) {
				App.bot.reactWait(e.getMessage());
				return;
			}
		}
		cooldown.put(authorId, System.currentTimeMillis());

		String[] args = Arrays.copyOfRange(msgArr, 1, msgArr.length);

		// Loop thru all the registered commands
		for (String cmd : registeredCommands.keySet()) {
			ArrayList<String> aliases = new ArrayList<String>(Arrays.asList(registeredCommands.get(cmd)));
			if ((userCmd.equals(cmd) || aliases.contains(userCmd)) && !ChatEventHandler.mute) {
				// Dispatch command
				registeredListeners.get(cmd).onCommand(e.getAuthor(), userCmd, args, e.getMessage(), e.getChannel(),
						e.getGuild());
				return;
			}
		}
		// Disabled cause.. spam i guess?
		// unknownCommand(userCmd, event);
		App.bot.reactQuestion(e.getMessage());
		EmoteDispatcher.register(e.getMessage(), new Help(App.bot), "kokoron_wut");
	}

	public static void unknownCommand(String cmd, User author, String command, String[] args, Message message,
			MessageChannel channel, Guild guild) {
		// No command found! => attempt to auto correct
		Object[] help = helpAttempt(cmd);
		String attempt = (String) help[0];
		double sim = (Double) help[1];
		if (sim >= 0.85) {
			registeredListeners.get(attempt).onCommand(author, command, args, message, channel, guild);
			return;
		}
		// No attempts are valid => send help message
		String helpMsg = BotConfig.UNKNOWN_COMMAND[new Random().nextInt(BotConfig.UNKNOWN_COMMAND.length)];
		if (sim >= 1.65) // Disabled this function
			helpMsg = BotConfig.COMMAND_SUGGESTION[new Random().nextInt(BotConfig.COMMAND_SUGGESTION.length)]
					.replace("#", attempt);
		App.bot.sendMessage(helpMsg, channel);
	}

	/**
	 * Register a command for the bot <br>
	 * Commands that are not registered will not be executed!
	 * 
	 * @param command  - command to register
	 * @param instance
	 */
	public static void register(String command, Command instance) {
		registeredCommands.put(command, new String[0]);
		registeredListeners.put(command, instance);
	}

	/**
	 * Register a command for the bot <br>
	 * Commands that are not registered will not be executed!
	 * 
	 * @param command - command to register
	 * @param aliases - command's aliases
	 */
	public static void register(String command, String[] aliases, Command instance) {
		registeredCommands.put(command, aliases);
		registeredListeners.put(command, instance);

	}

	/**
	 * Using the string similarity function to try to "save" the typo
	 * 
	 * @param userCmd - command that user typed
	 * @return (Object[]) { (String) "corrected" command and (double) similarity }
	 */
	public static Object[] helpAttempt(String userCmd) {
		String bestMatch = null;
		double bestDist = 0;
		for (String cmd : registeredCommands.keySet()) {
			double dist = StringTools.similarity(userCmd, cmd);
			if (dist > bestDist) {
				bestDist = dist;
				bestMatch = cmd;
			}
			for (String alias : registeredCommands.get(cmd)) {
				dist = StringTools.similarity(userCmd, alias);
				if (dist > bestDist) {
					bestDist = dist;
					bestMatch = cmd;
				}
			}
		}
		return new Object[] { bestMatch, bestDist };
	}
}
