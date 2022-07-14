package net.enderturret.itemsubs.init;

import net.minecraft.world.inventory.MenuType;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.enderturret.itemsubs.ItemSubs;
import net.enderturret.itemsubs.menu.SubmarineMenu;

public class ISMenus {

	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ItemSubs.MOD_ID);

	public static final RegistryObject<MenuType<?>> SUBMARINE = REGISTRY.register("submarine", () -> new MenuType<>(SubmarineMenu::new));
}