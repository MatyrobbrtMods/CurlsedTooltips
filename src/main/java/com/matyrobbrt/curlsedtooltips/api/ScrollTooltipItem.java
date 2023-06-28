package com.matyrobbrt.curlsedtooltips.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Implement this interface on items that have a scrollable tooltip.
 */
public interface ScrollTooltipItem {
    /**
     * Computes the {@linkplain ScrollTooltipComponent} the given {@code stack} shall display. <br>
     * <strong>NOTE:</strong> unlike {@link net.minecraft.world.item.Item#appendHoverText(ItemStack, Level, List, TooltipFlag)},
     * this method is only called ONCE in a tooltip cycle, to avoid the creation of too many objects and lists,
     * and will only be called for the same item if the displayed scrollable tooltip changes or the stack changes.
     *
     * @param stack the stack whose scrollable tooltips shall be computed
     * @return the stack's tooltips
     */
    List<ScrollTooltipComponent> computeScrollTooltip(ItemStack stack);
}
