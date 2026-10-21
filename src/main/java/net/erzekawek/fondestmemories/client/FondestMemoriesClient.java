package net.erzekawek.fondestmemories.client;

import net.erzekawek.fondestmemories.client.skyrendering.SkyPanelRenderer;
import net.erzekawek.fondestmemories.client.skyrendering.SkyWindowRenderer;
import net.erzekawek.fondestmemories.registry.FondestMemoriesBlockEntityRegistry;
import net.erzekawek.fondestmemories.registry.FondestMemoriesDimensionRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class FondestMemoriesClient implements ClientModInitializer {

    private static ResourceKey<Level> lastDimension = null;

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(FondestMemoriesBlockEntityRegistry.SKY_WINDOW, context -> new SkyWindowRenderer());
        BlockEntityRenderers.register(FondestMemoriesBlockEntityRegistry.SKY_PANEL_NOON, context -> new SkyPanelRenderer());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null) {
                lastDimension = null;
                return;
            }

            ResourceKey<Level> current = client.level.dimension();
            if (current.equals(lastDimension)) return;
            lastDimension = current;

            if (current.equals(FondestMemoriesDimensionRegistry.LIGHTS_OUT)) {
                client.options.renderDistance().set(8);
                client.options.gamma().set(0.0);
                client.options.vignette().set(false);
            }
        });
    }
}
