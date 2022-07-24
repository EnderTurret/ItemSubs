package net.enderturret.itemsubs;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ISConfig {

	static final ForgeConfigSpec SERVER_SPEC;
	private static final ISConfig INSTANCE;

	static {
		final var pair = new ForgeConfigSpec.Builder().configure(ISConfig::new);
		SERVER_SPEC = pair.getRight();
		INSTANCE = pair.getLeft();
	}

	private final BooleanValue realismMode;
	private final IntValue distanceFromFuel;
	private final DoubleValue speedUpgradeModifier;
	private final BooleanValue submarineExplosions;

	private ISConfig(ForgeConfigSpec.Builder builder) {
		realismMode = builder
				.comment("Whether \"realism mode\" is enabled.",
						"This currently affects whether you can place submarines in the air.",
						"This feature, while fun, isn't realistic and may be undesirable in most modded environments.")
				.define("realismMode", true);
		distanceFromFuel = builder
				.comment("The number of blocks a submarine will travel per item smeltable from fuel.",
						"For example, a piece of coal smelts 8 items, so a submarine can move 40 (8 * 5) blocks with one (assuming a distanceFromFuel value of 5.)")
				.defineInRange("distanceFromFuel", 5, 1, Integer.MAX_VALUE);
		speedUpgradeModifier = builder
				.comment("The modifier effect a single speed upgrade has on a submarine.",
						"This value increases linearly for every speed upgrade added.",
						"The exact formula is .5 + speedUpgradeModifier * upgradeCount.")
				.defineInRange("speedUpgradeModifier", .25, 0, Double.MAX_VALUE);
		submarineExplosions = builder
				.comment("Whether submarines explode on collision at high enough speeds.")
				.define("submarineExplosions", true);
	}

	public boolean realismMode() {
		return realismMode.get();
	}

	public int distanceFromFuel() {
		return distanceFromFuel.get();
	}

	public double speedUpgradeModifier() {
		return speedUpgradeModifier.get();
	}

	public boolean submarineExplosions() {
		return submarineExplosions.get();
	}

	public static ISConfig get() {
		return INSTANCE;
	}
}