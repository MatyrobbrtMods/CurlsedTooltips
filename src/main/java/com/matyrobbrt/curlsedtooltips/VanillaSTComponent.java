package com.matyrobbrt.curlsedtooltips;

import com.matyrobbrt.curlsedtooltips.api.ScrollTooltipComponent;
import com.matyrobbrt.curlsedtooltips.api.ScrollTooltipItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
@ParametersAreNonnullByDefault
public class VanillaSTComponent implements ClientTooltipComponent {
    private static final int MAX_HEIGHT = 90;
    public static int offset;
    public static int maxOffset;
    public static int biggestWidth;
    public static boolean isRenderingTooltip;
    public static boolean isCurrentlyRenderingTooltip;
    private static StackHash hash;
    private static com.matyrobbrt.curlsedtooltips.VanillaSTComponent lastComponent;

    private final List<ScrollTooltipComponent> tooltipComponents;
    private final Font font;
    private final int totalHeight, width;

    public VanillaSTComponent(List<ScrollTooltipComponent> tooltipComponents, Font font) {
        this.tooltipComponents = tooltipComponents;
        this.font = font;
        totalHeight = tooltipComponents.stream().mapToInt(comp -> comp.getHeight(font)).sum();
        width = tooltipComponents.stream().mapToInt(comp -> comp.getWidth(font)).max().orElse(0);
        maxOffset = Math.max(0, totalHeight - MAX_HEIGHT);
        offset = 0;
    }

    @Override
    public int getHeight() {
        return Math.min(MAX_HEIGHT, totalHeight);
    }

    @Override
    public int getWidth(Font font) {
        return width + (totalHeight > MAX_HEIGHT ? 8 : 0);
    }

    public static final ResourceLocation SCROLL_LIST = new ResourceLocation(CurlsedTooltips.MOD_ID, "textures/gui/scroll.png");
    public static final int TEXTURE_WIDTH = 6;
    public static final int TEXTURE_HEIGHT = 6;

    final List<ScrollTooltipComponent> toRender = new ArrayList<>();
    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource source) {
        toRender.clear();
        int height = 0, prevHeight = 0;
        for (final ScrollTooltipComponent component : tooltipComponents) {
            prevHeight = height;
            height += component.getHeight(this.font);
            if (height > (offset + MAX_HEIGHT)) {
                toRender.add(component); // Add and break, so that images can partially render but the next components won't
                break;
            } else if (height >= offset && prevHeight >= offset) { // Make sure the tooltip is fully contained
                toRender.add(component);
            }
        }

        height = 0;
        for (final ScrollTooltipComponent comp : toRender) {
            final var renderer = comp.getTextRenderer();
            if (renderer != null)
                renderer.renderText(matrix, source, this.font, x, y + height, MAX_HEIGHT - height);
            height += comp.getHeight(this.font);
        }
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        graphics.pose().pushPose();
        graphics.pose().translate(0D, 0D, 500D);
        if (totalHeight > MAX_HEIGHT) {
            drawScrollBar(graphics, x + Math.max(biggestWidth - 8, width), y + 2, 6, getHeight() - 4, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
        int height = 0;
        for (final ScrollTooltipComponent comp : toRender) {
            final var renderer = comp.getRenderer();
            if (renderer != null)
                renderer.render(graphics, font, x, y + height, MAX_HEIGHT - height);
            height += comp.getHeight(font);
        }
        graphics.pose().popPose();
    }

    @SubscribeEvent
    static void onScroll(ScreenEvent.MouseScrolled.Pre event) {
        if (isRenderingTooltip) {
            offset += event.getScrollDelta() * (Screen.hasShiftDown() ? - 25 : - 8);
            if (offset > maxOffset) offset = maxOffset;
            else if (offset < 0) offset = 0;
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    static void gather(RenderTooltipEvent.GatherComponents event) {
        if (event.getItemStack().getItem() instanceof ScrollTooltipItem && !Screen.hasControlDown()) {
            event.getTooltipElements().add(Either.left(
                    Component.literal("Hold ").append(Component.literal("CTRL").withStyle(ChatFormatting.AQUA)).append(" and scroll for details.")
            ));
        }
    }

    static void registerFactory(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(TC.class, tc -> {
            if (tc.hash.equals(com.matyrobbrt.curlsedtooltips.VanillaSTComponent.hash) && com.matyrobbrt.curlsedtooltips.VanillaSTComponent.lastComponent != null) {
                return com.matyrobbrt.curlsedtooltips.VanillaSTComponent.lastComponent;
            }
            com.matyrobbrt.curlsedtooltips.VanillaSTComponent.hash = tc.hash;
            return com.matyrobbrt.curlsedtooltips.VanillaSTComponent.lastComponent = new com.matyrobbrt.curlsedtooltips.VanillaSTComponent(
                    tc.item.computeScrollTooltip(tc.stack),
                    Minecraft.getInstance().font
            );
        });
    }

    protected void drawScrollBar(GuiGraphics matrix, int barX, int barY, int barWidth, int barHeight, int textureWidth, int textureHeight) {
        int scroll = (int)Math.min((float)(barHeight - 2 - 4), (float)offset / (float)maxOffset * (float)(barHeight - 6));
        RenderSystem.setShaderTexture(0, SCROLL_LIST);
        //Top border
        matrix.blit(SCROLL_LIST, barX, barY, 0, 0, textureWidth, 1, textureWidth, textureHeight);
        //Bottom border
        matrix.blit(SCROLL_LIST, barX, barY + barHeight, 0, 0, textureWidth, 1, textureWidth, textureHeight);

        for (int i = barY + 1; i < barY + barHeight; i++) {
            matrix.blit(SCROLL_LIST, barX, i, 0, 1, textureWidth, 1, textureWidth, textureHeight);
        }

        //Scroll bar
        matrix.blit(SCROLL_LIST, barX + 1, barY + 1 + scroll, 0, 2, 4, 4, textureWidth, textureHeight);
    }

    public record StackHash(Item item, int count, CompoundTag nbt) {
        public static StackHash from(ItemStack stack) {
            return new StackHash(stack.getItem(), stack.getCount(), stack.serializeNBT());
        }
    }

    public record TC(ItemStack stack, ScrollTooltipItem item, StackHash hash) implements TooltipComponent {}
}
