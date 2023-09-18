package net.enderturret.itemsubs.init;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.enderturret.itemsubs.ItemSubs;
import net.enderturret.itemsubs.entity.SubmarineEntity;

public final class ISEntityTypes {

	@Internal
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ItemSubs.MOD_ID);

	public static final RegistryObject<EntityType<SubmarineEntity>> SUBMARINE =
			REGISTRY.register("submarine", () -> EntityType.Builder.<SubmarineEntity>of(SubmarineEntity::new, MobCategory.MISC)
					.sized(0.0625F * 14, 0.625F).clientTrackingRange(10).build("itemsubs:submarine"));
}