package JProjects.BaseInfoBot.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeFormatter {
	// June 20, 2019 01:00:00
	public static Calendar getDateFromBandoriString(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMMMM dd, yyyy hh:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(str));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return c;
	}

	public static String formatCalendar(Calendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMMMM dd, yyyy hh:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String formatted = sdf.format(cal.getTime());
		return formatted;
	}

	public static String getCountDown(long start, long end, long now, boolean exact) {
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

		if (days > 99 && !exact)
			sb = new StringBuilder("a long time");

		if (mode.equals("before"))
			sb.insert(0, "In ");
		else if (mode.equals("during"))
			sb.insert(0, "Ends in ");
		else if (mode.equals("after")) {
			sb.setCharAt(0, sb.substring(0, 1).toUpperCase().charAt(0));
			sb.append(" ago");
		}
		return sb.toString();
	}

	public static String getCountDownSimple(long time) {
		long days = TimeUnit.MILLISECONDS.toDays(time);
		time -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(time);
		time -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
		time -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(time);

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
		if (sb.length() == 0)
			sb.append("1 second");
		return sb.toString();
	}

	public static String getCountDownApproxToMinutes(long time) {
		long days = TimeUnit.MILLISECONDS.toDays(time);
		time -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(time);
		time -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
		minutes = ((minutes + 5) / 10) * 10;
		time -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
		seconds = 0;

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
		if (sb.length() == 0)
			sb.append("under 5 minutes");
		else
			sb.insert(0, "around ");
		return sb.toString();
	}
}