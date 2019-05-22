package JProjects.BaseInfoBot.database.moe;

public enum MoeGrade {
	C, B, A, S, S2, S3, US;

	public static MoeGrade fromString(String s) {
		if (s == null)
			return null;
		if (s.equalsIgnoreCase("c"))
			return C;
		if (s.equalsIgnoreCase("b"))
			return B;
		if (s.equalsIgnoreCase("a"))
			return A;
		if (s.equalsIgnoreCase("s"))
			return S;
		if (s.equalsIgnoreCase("s2"))
			return S2;
		if (s.equalsIgnoreCase("s3") || s.equalsIgnoreCase("s3+1") || s.equalsIgnoreCase("s3+2")
				|| s.equalsIgnoreCase("s3+3"))
			return S3;
		if (s.equalsIgnoreCase("us") || s.equalsIgnoreCase("us+1") || s.equalsIgnoreCase("us+2")
				|| s.equalsIgnoreCase("us+3"))
			return US;
		else
			return null;
	}

	public String toString(int level) {
		switch (this) {
		case C:
			return "C";
		case B:
			return "B";
		case A:
			return "A";
		case S:
			return "S";
		case S2:
			return "S2";
		case S3:
			if (level <= 30)
				return "S3";
			else if (level <= 35)
				return "S3+1";
			else if (level <= 40)
				return "S3+2";
			else
				return "S3+3";
		case US:
			if (level <= 30)
				return "US";
			else if (level <= 40)
				return "US+1";
			else if (level <= 50)
				return "US+2";
			else
				return "US+3";
		default:
			return null;
		}
	}

	public static String getUsGradeFromString(int level) {
		if (level <= 30)
			return "US";
		else if (level <= 40)
			return "US+1";
		else if (level <= 50)
			return "US+2";
		else
			return "US+3";
	}

	public double getMaxLevel() {
		switch (this) {
		case C:
			return 10;
		case B:
			return 15;
		case A:
			return 20;
		case S:
			return 25;
		case S2:
			return 30;
		case S3:
			return 45;
		case US:
			return 60;
		default:
			return 1;
		}
	}
}
