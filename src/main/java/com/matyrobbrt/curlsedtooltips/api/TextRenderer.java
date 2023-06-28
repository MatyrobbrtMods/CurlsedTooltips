package com.matyrobbrt.curlsedtooltips.api;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

/**
 * Client-only functional interface used to provide text renderers in scrollable tooltips.
 *
 * @see ScrollTooltipComponent#getTextRenderer()
 */
@FunctionalInterface
public interface TextRenderer {
    void renderText(Matrix4f matrix, MultiBufferSource.BufferSource source, Font font, int x, int y, int maxHeight);
}
