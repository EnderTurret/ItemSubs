package net.enderturret.itemsubs.client.gui;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import net.enderturret.itemsubs.ItemSubs;
import net.enderturret.itemsubs.entity.SubmarineEntity;
import net.enderturret.itemsubs.menu.SubmarineMenu;

@Internal
public final class SubmarineGui extends AbstractContainerScreen<SubmarineMenu> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ItemSubs.MOD_ID, "textures/gui/submarine.png");
	private static final ResourceLocation DECOR_TEXTURE = new ResourceLocation(ItemSubs.MOD_ID, "textures/gui/decorative_submarine.png");

	private final SubmarineEntity sub;
	private final boolean decor;

	public SubmarineGui(SubmarineMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		sub = menu.submarine;
		imageWidth = 176;
		imageHeight = 204;
		inventoryLabelY = imageHeight - 94;

		if (sub != null) decor = sub.isDecorative();
		// I'm not sure if this is possible, but just in case...
		// Guess decorative based on if the first slot is the upgrade slot.
		else decor = menu.slots.get(0).index == 1;
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, decor ? DECOR_TEXTURE : TEXTURE);

		blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

		if (sub != null && !decor) {
			final int burnTime = sub.getBurnTime();
			final int burnMax = sub.getBurnMax();

			if (burnTime > 0 && burnMax > 0) {
				final int progress = burnTime * 13 / burnMax;
				blit(poseStack,
						leftPos + 62, topPos + 19 + 12 - progress,
						176, 12 - progress,
						14, progress + 1);
			}
		}
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTick);
		renderTooltip(poseStack, mouseX, mouseY);
	}
}