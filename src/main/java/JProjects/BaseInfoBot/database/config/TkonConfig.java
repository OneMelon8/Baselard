package JProjects.BaseInfoBot.database.config;

public class TkonConfig {
	/*
	 * Logic
	 */
	public static final int MAX_SKILLS = 4;
	public static final int BAR_LENGTH_EXP = 31;
	public static final int BAR_LENGTH_INFO = 30;

	// Linear Leveling: EXP(level) = EXP_M(level) + EXP_B
	public static final int EXP_M = 20; // Exp increase per level
	public static final int EXP_B = 100; // Starting exp
	public static final int EXP_MAJOR_LEVEL = 10; // Every x levels is a "major" level, gain a random skill

	// Skill mutation chances
	public static final double MUTATION_SAME_CLASS = 0.15; // Skill mutations within same class (grass => earth)
	public static final double MUTATION_ACROSS_SPECIFICATIONS = 0.04; // Same class but across spec (air => ice)
	public static final double MUTATION_COMPLETELY_RANDOM = 0.01; // Completely random mutations (grass => ???)

	// Max percentage stats
	public static final double MAX_CRITICAL_CHANCE = 0.33;
	public static final double MAX_CRITICAL_DAMAGE = 2.50;
	public static final double MAX_RESISTANCE = 0.33;

	// Starting stats
	public static final int STARTING_HP = 100;
	public static final int STARTING_HP_RANDOMNESS = 10;
	public static final int STARTING_MP = 50;
	public static final int STARTING_MP_RANDOMNESS = 20;
	public static final int STARTING_ATTACK = 10;
	public static final int STARTING_ATTACK_RANDOMNESS = 5;
	public static final int STARTING_DEFENSE = 2;
	public static final int STARTING_DEFENSE_RANDOMNESS = 1;
	public static final int STARTING_SPEED = 5;
	public static final int STARTING_SPEED_RANDOMNESS = 2;
	public static final double STARTING_CRITICAL_CHANCE = 0.03;
	public static final double STARTING_CRITICAL_CHANCE_RANDOMNESS = 0.03;
	public static final double STARTING_CRITICAL_DAMAGE = 1.10;
	public static final double STARTING_CRITICAL_DAMAGE_RANDOMNESS = 0.05;
	public static final int STARTING_ACCURACY = 4;
	public static final int STARTING_ACCURACY_RANDOMNESS = 1;
	public static final int STARTING_EVASION = 1;
	public static final int STARTING_EVASION_RANDOMNESS = 1;
	public static final double STARTING_RESISTANCE = 0.03;
	public static final double STARTING_RESISTANCE_RANDOMNESS = 0.02;

	// Leveling up stats increase
	public static final int LEVELING_HP = 10;
	public static final int LEVELING_HP_RANDOMNESS = 5;
	public static final int LEVELING_MP = 1;
	public static final int LEVELING_MP_RANDOMNESS = 1;
	public static final int LEVELING_ATTACK = 3;
	public static final int LEVELING_ATTACK_RANDOMNESS = 1;
	public static final int LEVELING_DEFENSE = 1;
	public static final int LEVELING_DEFENSE_RANDOMNESS = 1;
	public static final int LEVELING_SPEED = 1;
	public static final int LEVELING_SPEED_RANDOMNESS = 1;
	public static final double LEVELING_CRITICAL_CHANCE = 0.00012;
	public static final double LEVELING_CRITICAL_CHANCE_RANDOMNESS = 0.005;
	public static final double LEVELING_CRITICAL_DAMAGE = 0.001;
	public static final double LEVELING_CRITICAL_DAMAGE_RANDOMNESS = 0.005;
	public static final int LEVELING_ACCURACY = 2;
	public static final int LEVELING_ACCURACY_RANDOMNESS = 1;
	public static final int LEVELING_EVASION = 1;
	public static final int LEVELING_EVASION_RANDOMNESS = 1;
	public static final double LEVELING_RESISTANCE = 0.00015;
	public static final double LEVELING_RESISTANCE_RANDOMNESS = 0.005;

	/*
	 * Combat
	 */
	public static final long HEARTBEAT_INTERVAL = 3000; // MS

	/*
	 * Images
	 */
	public static final double SCALE_DISPLAY = 0.8; // from 128px to 102.4px
	public static final double SCALE_INVENTORY = 0.4; // from 128px to 51.2px

	/*
	 * Utils
	 */
	public static double calcRange(int tileRange, boolean diagonal) {
		if (!diagonal)
			return tileRange;
		return Math.sqrt(Math.pow(tileRange, 2) * 2);
	}

	/*
	 * Standard statistics (ignoring randomness)
	 */
	public static int getStandardHp(int level) {
		return STARTING_HP + LEVELING_HP * (level - 1);
	}

	public static int getStandardMp(int level) {
		return STARTING_MP + LEVELING_MP * (level - 1);
	}

	public static int getStandardAttack(int level) {
		return STARTING_ATTACK + LEVELING_ATTACK * (level - 1);
	}

	public static int getStandardDefense(int level) {
		return STARTING_DEFENSE + LEVELING_DEFENSE * (level - 1);
	}

	public static int getStandardSpeed(int level) {
		return STARTING_SPEED + LEVELING_SPEED * (level - 1);
	}

	public static double getStandardCriticalChance(int level) {
		return Math.min(STARTING_CRITICAL_CHANCE + LEVELING_CRITICAL_CHANCE * (level - 1), MAX_CRITICAL_CHANCE);
	}

	public static double getStandardCriticalDamage(int level) {
		return Math.min(STARTING_CRITICAL_DAMAGE + LEVELING_CRITICAL_DAMAGE * (level - 1), MAX_CRITICAL_DAMAGE);
	}

	public static int getStandardAccuracy(int level) {
		return STARTING_ACCURACY + LEVELING_ACCURACY * (level - 1);
	}

	public static int getStandardEvasion(int level) {
		return STARTING_EVASION + LEVELING_EVASION * (level - 1);
	}

	public static double getStandardResistance(int level) {
		return Math.min(STARTING_RESISTANCE + LEVELING_RESISTANCE * (level - 1), MAX_RESISTANCE);
	}
}
