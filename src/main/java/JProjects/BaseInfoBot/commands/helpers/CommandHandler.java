package JProjects.BaseInfoBot.commands.helpers;

import JProjects.BaseInfoBot.BaseInfoBot;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

public abstract class CommandHandler {
	protected String command;
	protected String[] aliases;

	protected String helpMsg;

	protected BaseInfoBot bot;

	/**
	 * Constructor
	 * 
	 * @param command - the command string
	 * @param helpMsg - the help message
	 */
	public CommandHandler(BaseInfoBot bot, String command, String helpMsg) {
		this.bot = bot;
		this.command = command;
		this.aliases = new String[0];
		this.helpMsg = helpMsg;

		CommandDispatcher.register(command, this);
	}

	/**
	 * Constructor
	 * 
	 * @param command - the command string
	 * @param aliases - aliases to the command
	 * @param helpMsg - the help message
	 */
	public CommandHandler(BaseInfoBot bot, String command, String[] aliases, String helpMsg) {
		this.bot = bot;
		this.command = command;
		this.aliases = aliases;
		this.helpMsg = helpMsg;

		CommandDispatcher.register(command, aliases, this);
	}

	/**
	 * Executes the command
	 * 
	 * @param guild
	 */
	public abstract void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild);

	/**
	 * @return (String) help message
	 */
	public String getHelp() {
		return this.helpMsg;
	}

	public abstract MessageEmbed getHelpEmbeded();
}
