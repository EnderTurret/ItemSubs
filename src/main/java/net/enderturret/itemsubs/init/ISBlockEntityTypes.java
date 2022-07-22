package net.enderturret.itemsubs.init;

import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.enderturret.itemsubs.ItemSubs;
import net.enderturret.itemsubs.block.entity.SubmarineStationBlockEntity;

public class ISBlockEntityTypes {

	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ItemSubs.MOD_ID);

	public static final RegistryObject<BlockEntityType<? extends SubmarineStationBlockEntity>> SUBMARINE_STATION = REGISTRY.register("submarine_station",
			() -> BlockEntityType.Builder.of(SubmarineStationBlockEntity::new, ISBlocks.SUBMARINE_STATION.get()).build(null));
}