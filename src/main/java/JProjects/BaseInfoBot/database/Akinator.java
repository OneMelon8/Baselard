package JProjects.BaseInfoBot.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.Akiwrapper.Answer;
import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.entities.Question;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.utils.Servers;

import JProjects.BaseInfoBot.database.config.AkinatorConfig;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

public class Akinator {

	private User user;
	private long startTime;
	private Akiwrapper aw;

	private HashMap<Question, Integer> log;
	private int round;
	private int status; // 0=normal, 1=stuck
	private Question question;
	private Guess guess;

	public Akinator(User user) {
		this(user, false);
	}

	public Akinator(User user, boolean enableNsfw) {
		this.user = user;
		this.startTime = System.currentTimeMillis();
		this.aw = new AkiwrapperBuilder().setName(this.user.getName()).setFilterProfanity(!enableNsfw)
				.setLocalization(Language.ENGLISH).setServer(Servers.getFirstAvailableServer(Language.ENGLISH))
				.setUserAgent("Chrome").build();

		this.log = new HashMap<Question, Integer>();
		this.round = 1;
		this.question = aw.getCurrentQuestion();
	}

	// Answer codes: 0=no, 1=prob not, 2=dunno, 3=prob, 4=yes, 5=stuck
	public MessageEmbed next(int answer) throws IOException {
		this.round++;
		this.log.put(this.question, answer);
		this.question = this.aw.answerCurrentQuestion(this.getAnswerFromIndex(answer));
		List<Guess> guesses = this.aw.getGuessesAboveProbability(AkinatorConfig.CONFIDENCE);

		if (!guesses.isEmpty()) {
			this.guess = guesses.get(0);
			return this.getGuessEmbeded();
		}

		if (this.question == null) {
			// TODO: handle stuck
			this.status = 1;
			guesses = this.aw.getGuesses(); // get all guesses regardless of probability

			if (guesses.isEmpty()) {
				return null; // return win embeded!
			}

			return null; // guesses embeded
		}

		return this.getQuestionEmbeded();
	}

	private MessageEmbed getQuestionEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(AkinatorConfig.COLOR_EMBEDED);
		builder.setAuthor("Akinator Round " + this.round + " -- " + this.user.getName());
		builder.setDescription("Current Progress: " + GeneralTools.round(this.question.getProgression(), 2) + "% ("
				+ GeneralTools.signNumber(GeneralTools.round(this.question.getProgression(), 2)) + "%)");

		builder.addField("**" + this.question.getQuestion() + "**",
				"Choices: No / Probably Not / Don't Know / Probably / Yes", false);
		return builder.build();
	}

	private MessageEmbed getGuessEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(AkinatorConfig.COLOR_EMBEDED);
		builder.setAuthor("Akinator Round " + this.round + " -- " + this.user.getName());
		builder.setDescription("Confidence: " + GeneralTools.round(this.guess.getProbability(), 2));
		if (this.guess.getImage() != null)
			builder.setImage(this.guess.getImage().toString());

		builder.addField("**" + this.guess.getName() + "**", this.guess.getDescription(), false);
		return builder.build();
	}

	private Answer getAnswerFromIndex(int index) {
		switch (index) {
		case 0:
			return Answer.NO;
		case 1:
			return Answer.PROBABLY_NOT;
		case 2:
			return Answer.DONT_KNOW;
		case 3:
			return Answer.PROBABLY;
		case 4:
			return Answer.YES;
		default:
			return null;
		}
	}

	// GENERATED
	public static double getConfidence() {
		return AkinatorConfig.CONFIDENCE;
	}

	public User getUser() {
		return user;
	}

	public long getStartTime() {
		return startTime;
	}

	public Akiwrapper getAw() {
		return aw;
	}

	public HashMap<Question, Integer> getLog() {
		return log;
	}

	public int getRound() {
		return round;
	}

	public int getStatus() {
		return status;
	}

	public Question getQuestion() {
		return question;
	}

	public Guess getBestGuess() {
		return guess;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setAw(Akiwrapper aw) {
		this.aw = aw;
	}

	public void setLog(HashMap<Question, Integer> log) {
		this.log = log;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public void setBestGuess(Guess bestGuess) {
		this.guess = bestGuess;
	}

}
