package net.enderturret.itemsubs.entity;

public enum LockedState {

	FUEL(1),
	UPGRADES(2),
	INVENTORY(4),
	STATUS(8);

	private final byte pos;

	private LockedState(int pos) {
		this.pos = (byte) pos;
	}

	public byte set(byte flags, boolean on) {
		return (byte) (on ? flags | pos : flags & ~pos);
	}

	public boolean get(byte flags) {
		return (flags & pos) != 0;
	}
}