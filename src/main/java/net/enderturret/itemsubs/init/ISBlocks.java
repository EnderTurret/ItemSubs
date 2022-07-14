package net.enderturret.itemsubs.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.enderturret.itemsubs.ItemSubs;
import net.enderturret.itemsubs.block.SubmarineRelayBlock;
import net.enderturret.itemsubs.block.SubmarineStationBlock;

public class ISBlocks {

	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, ItemSubs.MOD_ID);

	public static final RegistryObject<Block> SUBMARINE_RELAY = REGISTRY.register("submarine_relay", () -> new SubmarineRelayBlock(props(Material.METAL)));
	public static final RegistryObject<Block> SUBMARINE_STATION = REGISTRY.register("submarine_station", () -> new SubmarineStationBlock(props(Material.METAL)));

	private static Block.Properties props(Material material) {
		return Block.Properties.of(material);
	}
}