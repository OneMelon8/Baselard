package JProjects.BaseInfoBot.tools;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

public class StringTools {
	public static String truncateAfter(String s, int index) {
		if (s.length() <= index)
			return s;
		return s.substring(0, index);
	}

	public static String smartReplaceNonEnglish(String s) {
		String result = s.replaceAll("[^\\x00-\\x7F]", "");
		return result.isEmpty() ? s : result;
	}

	public static String replaceNonEnglish(String s) {
		return s.replaceAll("[^\\x00-\\x7F]", "");
	}

	public static String removeDoubleSpaces(String s) {
		while (s.contains("  "))
			s = s.replace("  ", " ");
		return s;
	}

	public static String scrambleVisible(String sentence) {
		String[] data = sentence.trim().split(" ");
		StringBuilder sb = new StringBuilder();
		for (String word : data) {
			ArrayList<Tuple> puncts = new ArrayList<Tuple>();
			if (containsRegex("\\W", word)) {
				for (int a = 0; a < word.length(); a++) {
					String sub = word.substring(a, a + 1);
					if (!sub.matches("\\W"))
						continue;
					puncts.add(new Tuple(a, sub));
				}
				word = word.replaceAll("\\W", "");
			}
			if (word.length() <= 3) {
				sb.append(restorePuncts(word, puncts) + " ");
				continue;
			}

			String output = word.charAt(0) + scramble(word.substring(1, word.length() - 1))
					+ word.charAt(word.length() - 1);
			sb.append(restorePuncts(output, puncts) + " ");
		}
		return sb.toString().trim();
	}

	public static boolean containsRegex(String pattern, String text) {
		return Pattern.compile(pattern).matcher(text).find();
	}

	private static String restorePuncts(String original, ArrayList<Tuple> puncts) {
		if (puncts.isEmpty())
			return original;
		for (Tuple t : puncts) {
			int index = (int) t.getObj1();
			String punct = (String) t.getObj2();
			original = original.substring(0, index) + punct + original.substring(index);
		}
		return original;
	}

	public static void main(String[] args) {
		String s = "According to a researcher at Cambridge University, "
				+ "it doesn't matter in what order the letters in a word are, "
				+ "the only important thing is that the first and last letter be at the right place. "
				+ "The rest can be a total mess and you can still read it without problem. "
				+ "This is because the human mind does not read every letter by itself but the word as a whole.";
		System.out.println(scrambleVisible(s));
	}

	public static String scramble(String s) {
		return new String(shuffleCharArray(s.toCharArray()));
	}

	private static char[] shuffleCharArray(char[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			char a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
		return ar;
	}

	/**
	 * Calculates the similarity (a number within 0 and 1) between two strings.
	 */
	public static double similarity(String s1, String s2) {
		String longer = s1, shorter = s2;
		if (s1.length() < s2.length()) { // longer should always have greater length
			longer = s2;
			shorter = s1;
		}
		int longerLength = longer.length();
		if (longerLength == 0) {
			return 1.0;
		}

		return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	}

	private static int editDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0)
					costs[j] = j;
				else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
							newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}
}
