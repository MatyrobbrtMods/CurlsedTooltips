package com.matyrobbrt.curlsedtooltips.api;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Client-only functional interface used to provide image renderers in scrollable tooltips.
 *
 * @see ScrollTooltipComponent#getRenderer()
 */
@FunctionalInterface
public interface Renderer {
    void render(GuiGraphics graphics, Font font, int x, int y, int maxHeight);
}
