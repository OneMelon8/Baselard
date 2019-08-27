package JProjects.BaseInfoBot.tools;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GeneralTools {
	public static Number getNumeric(String s) {
		return getNumeric(s, false);
	}

	public static Number getNumeric(double d) {
		return getNumeric(Double.toString(d), false);
	}

	public static Number getNumeric(String s, boolean forceDouble) {
		if (s == null || s.isEmpty() || s.equalsIgnoreCase("n/a") || s.equalsIgnoreCase("na"))
			return forceDouble ? -1D : -1;
		s = s.trim();
		try {
			double d = Double.parseDouble(s);
			if (forceDouble)
				return d;
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				return d;
			}
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	public static boolean isNumeric(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static String getBar(double current, double max, double length) {
		length -= 2;
		final double percentage = Math.min(1.0, Math.round(current / max * 100D) / 100D);
		final int hashCount = (int) (percentage * length);
		final int dashCount = (int) (length - hashCount);

		StringBuilder sb = new StringBuilder("[");
		for (int a = 0; a <= hashCount; a++)
			sb.append("#");
		for (int a = 0; a < dashCount; a++)
			sb.append("-");
		sb.append("]");
		return sb.toString();
	}

	public static String signNumber(double d) {
		return (d < 0 ? "" : "+") + d;
	}

	public static double round(double d, int places) {
		return Math.round(d * Math.pow(10, places)) / Math.pow(10, places);
	}

	public static String shortenDouble(double d) {
		if (d / 1000D > 1) {
			return Math.round((d - d % 1000) / 1000) + "k";
		}
		return null;
	}

	public static String toBinary(String msg) {
		byte[] bytes = msg.getBytes();
		StringBuilder binary = new StringBuilder();
		for (byte b : bytes) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				binary.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
			binary.append(' ');
		}
		return binary.toString();
	}

	public static LinkedHashMap<Object, Double> sortByValueDouble(HashMap<Object, Double> hm) {
		List<Map.Entry<Object, Double>> list = new LinkedList<Map.Entry<Object, Double>>(hm.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Object, Double>>() {
			public int compare(Map.Entry<Object, Double> o1, Map.Entry<Object, Double> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		LinkedHashMap<Object, Double> temp = new LinkedHashMap<Object, Double>();
		for (Map.Entry<Object, Double> aa : list)
			temp.put(aa.getKey(), aa.getValue());
		return temp;
	}

	public static LinkedHashMap<Object, Long> sortByValueLong(HashMap<Object, Long> hm) {
		List<Map.Entry<Object, Long>> list = new LinkedList<Map.Entry<Object, Long>>(hm.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Object, Long>>() {
			public int compare(Map.Entry<Object, Long> o1, Map.Entry<Object, Long> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		LinkedHashMap<Object, Long> temp = new LinkedHashMap<Object, Long>();
		for (Map.Entry<Object, Long> aa : list)
			temp.put(aa.getKey(), aa.getValue());
		return temp;
	}

	public static Object getLinkedMapKeyByIndex(LinkedHashMap<Object, Object> map, int index) {
		return map.keySet().toArray()[index];
	}

	public static Object getLinkedMapValueByIndex(LinkedHashMap<Object, Object> map, int index) {
		return map.values().toArray()[index];
	}

	public static String getTime() {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
	}

	public static void logError(Exception ex) {
		System.out.println(getTime() + " >> " + ex.getClass().getSimpleName() + " : " + ex.getMessage());
		ex.printStackTrace();
	}
}
