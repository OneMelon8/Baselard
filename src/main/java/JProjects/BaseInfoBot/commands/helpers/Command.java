package JProjects.BaseInfoBot.commands.helpers;

import JProjects.BaseInfoBot.BaseInfoBot;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Command {
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
	public Command(BaseInfoBot bot, String command, String helpMsg) {
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
	public Command(BaseInfoBot bot, String command, String[] aliases, String helpMsg) {
		this.bot = bot;
		this.command = command;
		this.aliases = aliases;
		this.helpMsg = helpMsg;

		CommandDispatcher.register(command, aliases, this);
	}

	/**
	 * Executes the command
	 */
	public abstract void onCommand(MessageReceivedEvent event);

	/**
	 * @return (String) help message
	 */
	public String getHelp() {
		return this.helpMsg;
	}

	public abstract MessageEmbed getHelpEmbeded();
}
