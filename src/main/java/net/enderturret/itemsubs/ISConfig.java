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
		realismMode = builder.define("realismMode", true);
		distanceFromFuel = builder.defineInRange("distanceFromFuel", 5, 1, Integer.MAX_VALUE);
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