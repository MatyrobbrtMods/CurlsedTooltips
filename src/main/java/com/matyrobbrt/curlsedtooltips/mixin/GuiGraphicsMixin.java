package com.matyrobbrt.curlsedtooltips.mixin;

import com.matyrobbrt.curlsedtooltips.VanillaSTComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Inject(at = @At("HEAD"), method = "renderTooltipInternal", cancellable = true)
    private void scrollabletooltips$cancelNonScroll(Font pFont, List<ClientTooltipComponent> pComponents, int pMouseX, int pMouseY, ClientTooltipPositioner pTooltipPositioner, CallbackInfo ci) {
        if (VanillaSTComponent.isRenderingTooltip && !VanillaSTComponent.isCurrentlyRenderingTooltip) {
            ci.cancel();
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;", shift = At.Shift.BEFORE), method = "renderTooltipInternal", locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void scrollabletooltips$captureBiggestWidth(Font font, List p_169385_, int p_169386_, int p_169387_, ClientTooltipPositioner positioner, CallbackInfo ci, RenderTooltipEvent.Pre preEvent, int i, int j, int j2, int k2) {
        VanillaSTComponent.biggestWidth = i;
    }

    @ModifyConstant(method = "renderTooltipInternal", constant = @Constant(floatValue = 400))
    private float scrollabletooltips$pushFurthest(float value) {
        return VanillaSTComponent.isRenderingTooltip ? 500 : value;
    }
}
