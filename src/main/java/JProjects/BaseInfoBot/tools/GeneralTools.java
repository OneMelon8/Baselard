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
		} catch (NumberFormatException nfe) {
			return false;
		}
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

	public static LinkedHashMap<Object, Double> sortByValue(HashMap<Object, Double> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<Object, Double>> list = new LinkedList<Map.Entry<Object, Double>>(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<Object, Double>>() {
			public int compare(Map.Entry<Object, Double> o1, Map.Entry<Object, Double> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// put data from sorted list to hashmap
		LinkedHashMap<Object, Double> temp = new LinkedHashMap<Object, Double>();
		for (Map.Entry<Object, Double> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	public static Object getLinkedMapValueByIndex(LinkedHashMap<Object, Object> map, int index) {
		return map.values().toArray()[index];
	}

	public static String getTime() {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
	}

	public static void logError(Exception ex) {
		System.out.println(getTime() + " >> " + ex.getClass().getSimpleName() + " : " + ex.getMessage());
	}
}
