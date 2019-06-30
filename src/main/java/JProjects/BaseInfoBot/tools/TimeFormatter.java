package JProjects.BaseInfoBot.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeFormatter {
	private static HashMap<String, Integer> dateReplacer = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 5861217683916303222L;
		{
			put("January", 1);
			put("February", 2);
			put("March", 3);
			put("April", 4);
			put("May", 5);
			put("June", 6);
			put("July", 7);
			put("August", 8);
			put("September", 9);
			put("October", 10);
			put("November", 11);
			put("December", 12);
		}
	};

	// June 20, 2019 01:00:00
	public static Calendar getDateFromBandoriString(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("M dd, yyyy hh:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Calendar c = Calendar.getInstance();

		for (Entry<String, Integer> entry : dateReplacer.entrySet())
			str = str.replace(entry.getKey(), String.valueOf(entry.getValue()));

		try {
			c.setTime(sdf.parse(str));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return c;
	}

	public static String getCountDown(long start, long end, long now) {
		long diff = 0;
		String mode = "";
		if (start > now) {
			diff = start - now;
			mode = "before";
		} else if (start < now && end > now) {
			diff = end - now;
			mode = "during";
		} else if (end < now) {
			diff = now - end;
			mode = "after";
		}

		diff = Math.abs(diff);
		long days = TimeUnit.MILLISECONDS.toDays(diff);
		diff -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(diff);
		diff -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
		diff -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);

		StringBuilder sb = new StringBuilder();
		if (days > 0)
			sb.append(days + (days == 1 ? " day " : " days "));
		if (hours > 0)
			sb.append(hours + (hours == 1 ? " hour " : " hours "));
		if (minutes > 0)
			sb.append(minutes + (minutes == 1 ? " minute " : " minutes "));
		if (seconds > 0)
			sb.append(seconds + (seconds == 1 ? " second" : " seconds"));

		if (days > 99)
			sb = new StringBuilder("a long time");

		if (mode.equals("before"))
			sb.insert(0, "In ");
		else if (mode.equals("during"))
			sb.insert(0, "Ends in ");
		else if (mode.equals("after")) {
			sb.setCharAt(0, sb.substring(0, 1).toUpperCase().charAt(0));
			sb.append(" ago");
		}
		return (sb.toString());
	}
}