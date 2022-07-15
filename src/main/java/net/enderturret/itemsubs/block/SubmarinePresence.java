package net.enderturret.itemsubs.block;

import net.minecraft.util.StringRepresentable;

public enum SubmarinePresence implements StringRepresentable {

	NOT_PRESENT("not_present"),
	PRESENT("present"),
	TURNING("turning");

	private final String id;

	private SubmarinePresence(String id) {
		this.id = id;
	}

	@Override
	public String getSerializedName() {
		return id;
	}
}