package com.matyrobbrt.curlsedtooltips.mixin;

import com.matyrobbrt.curlsedtooltips.VanillaSTComponent;
import com.matyrobbrt.curlsedtooltips.api.ScrollTooltipItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {

    @Shadow @Nullable protected Slot hoveredSlot;

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Unique
    private Slot scrollabletooltips$tooltipRenderSlot;
    @Unique
    private int scrollabletooltips$tooltipRenderX;
    @Unique
    private int scrollabletooltips$tooltipRenderY;

    @Inject(at = @At("HEAD"), method = "renderTooltip")
    protected void scrollabletooltips$renderTooltip(GuiGraphics graphics, int x, int y, CallbackInfo ci) {
        if (scrollabletooltips$tooltipRenderSlot != null) {
            if (Screen.hasControlDown()) {
                VanillaSTComponent.isCurrentlyRenderingTooltip = true;
                final var item = scrollabletooltips$tooltipRenderSlot.getItem();
                final VanillaSTComponent.StackHash hash = VanillaSTComponent.StackHash.from(item);
                graphics.renderTooltip(minecraft.font, getTooltipFromItem(minecraft, item), Optional.of(new VanillaSTComponent.TC(item, ((ScrollTooltipItem) item.getItem()), hash)), item, scrollabletooltips$tooltipRenderX ,scrollabletooltips$tooltipRenderY);
                VanillaSTComponent.isCurrentlyRenderingTooltip = false;
            } else {
                VanillaSTComponent.isRenderingTooltip = false;
                scrollabletooltips$tooltipRenderSlot = null;
            }
        }
    }
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;Lnet/minecraft/world/item/ItemStack;II)V", remap = false), method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V")
    protected void scrollabletooltips$store(GuiGraphics stack, int x, int y, CallbackInfo ci) {
        if (hoveredSlot != null && Screen.hasControlDown() && hoveredSlot.getItem().getItem() instanceof ScrollTooltipItem && hoveredSlot != scrollabletooltips$tooltipRenderSlot) {
            scrollabletooltips$tooltipRenderSlot = hoveredSlot;
            scrollabletooltips$tooltipRenderX = x;
            scrollabletooltips$tooltipRenderY = y;
            VanillaSTComponent.isRenderingTooltip = true;
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;Lnet/minecraft/world/item/ItemStack;II)V", remap = false), method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V")
    protected void scrollabletooltips$notRenderAgain(GuiGraphics instance, Font font, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, ItemStack stack, int mouseX, int mouseY) {
        if (scrollabletooltips$tooltipRenderSlot == null) {
            instance.renderTooltip(font, textComponents, tooltipComponent, stack, mouseX, mouseY);
        }
    }
}
