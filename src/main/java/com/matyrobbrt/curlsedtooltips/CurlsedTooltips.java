package com.matyrobbrt.curlsedtooltips;

import com.matyrobbrt.curlsedtooltips.api.ScrollTooltipComponent;
import com.matyrobbrt.curlsedtooltips.api.ScrollTooltipItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;

@Mod(CurlsedTooltips.MOD_ID)
public class CurlsedTooltips {
    public static final String MOD_ID = "curlsedtooltips";

    public CurlsedTooltips() {
        if (!FMLEnvironment.production) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener((final RegisterEvent event) ->
                    event.register(Registries.ITEM, helper ->
                            helper.register("test_tooltip", new TestTooltipItem(new Item.Properties()))));
        }

        if (FMLEnvironment.dist.isClient()) {
            initClient();
        }
    }

    private void initClient() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(VanillaSTComponent::registerFactory);
        MinecraftForge.EVENT_BUS.register(VanillaSTComponent.class);
    }

    private static final class TestTooltipItem extends Item implements ScrollTooltipItem {
        public TestTooltipItem(Properties properties) {
            super(properties);
        }

        @Override
        public List<ScrollTooltipComponent> computeScrollTooltip(ItemStack stack) {
            final List<ScrollTooltipComponent> components = new ArrayList<>();
            for (int i = 0; i < 90; i++) {
                if (i == 50) {
                    components.add(ScrollTooltipComponent.image(
                            new ResourceLocation("textures/map/map_background.png"),
                            64, 64
                    ));
                }
                components.add(ScrollTooltipComponent.text((i % 2 == 0 ? "Lorem" : "Ipsum") + " " + i));
            }
            return components;
        }
    }
}
