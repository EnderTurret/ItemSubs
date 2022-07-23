package net.enderturret.itemsubs;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
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
	}

	public boolean realismMode() {
		return realismMode.get();
	}

	public int distanceFromFuel() {
		return distanceFromFuel.get();
	}

	public static ISConfig get() {
		return INSTANCE;
	}
}