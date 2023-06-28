package com.matyrobbrt.curlsedtooltips.api;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * A component similar to {@link net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent} that is used to
 * display scrollable tooltips.
 *
 * @see ScrollTooltipItem#computeScrollTooltip(ItemStack)
 */
public interface ScrollTooltipComponent {
    /**
     * {@return the image renderer of the component}
     * <strong>NOTE:</strong> do not call unless on the client.
     */
    @Nullable
    default Renderer getRenderer() {
        return null;
    }

    /**
     * {@return the text renderer of the component}
     * <strong>NOTE:</strong> do not call unless on the client.
     */
    @Nullable
    default TextRenderer getTextRenderer() {
        return null;
    }

    /**
     * {@return the width of the component for the given {@code font}}
     */
    int getWidth(Font font);

    /**
     * {@return the height of the component for the given {@code font}}
     */
    int getHeight(Font font);

    static Text text(Component component) {
        return new Text(component);
    }

    static Text text(String text) {
        return new Text(Component.literal(text));
    }

    static Image image(ResourceLocation location, int width, int height) {
        return new Image(location, width, height);
    }

    record Text(Component component) implements ScrollTooltipComponent {
        @Override
        public int getWidth(Font font) {
            return font.width(component);
        }

        @Override
        public int getHeight(Font font) {
            return font.lineHeight;
        }

        @Override
        public TextRenderer getTextRenderer() {
            return (matrix, source, font, x, y, maxHeight) -> {
                if (getHeight(font) <= maxHeight) {
                    font.drawInBatch(component, x, y, -1, true, matrix, source, Font.DisplayMode.NORMAL, 0, 15728880);
                }
            };
        }
    }

    record Image(ResourceLocation location, int textureWidth, int textureHeight) implements ScrollTooltipComponent {

        @Override
        public int getWidth(Font font) {
            return textureWidth;
        }

        @Override
        public int getHeight(Font font) {
            return textureHeight;
        }

        @Override
        public Renderer getRenderer() {
            return (graphics, font, x, y, maxHeight) -> graphics.blit(location, x, y, 0, 0, textureWidth, Math.min(textureHeight, maxHeight), textureWidth, textureHeight);
        }
    }

}
